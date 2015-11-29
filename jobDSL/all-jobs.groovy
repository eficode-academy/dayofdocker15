job('tag-version'){
  logRotator( -1, 5 ,-1 ,-1 )
  scm {
    git {
      remote {
        name('origin')
        url('https://github.com/drbosse/dayofdocker15.git')
      }
      branch('master')
      configure {
        it / 'extensions' << 'hudson.plugins.git.extensions.impl.PathRestriction' {
          'includedRegions' 'version.txt'
        }
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
  }
  steps {
    shell('''#!/bin/bash
[ ! -f version.txt ] && exit 1
VERSION=$(cat version.txt)
echo VERSION=${VERSION} > version.env''')
    environmentVariables {
        propertiesFile('version.env')
    }
  }
  publishers {
        git {
            pushOnlyIfSuccess()
            tag('origin', '$VERSION') {
                message('Tag $VERSION')
                create()
                update()
            }
        }
    }
}

job('build-browser') {
  logRotator( -1, 5 ,-1 ,-1 )
  scm {
    git {
      remote {
        name('origin')
        url('https://github.com/drbosse/dayofdocker15.git')
      }
      branch('master')
      configure {
        it / 'extensions' << 'hudson.plugins.git.extensions.impl.PathRestriction' {
          'includedRegions' '''GoWebBrowser/.*\\.go
GoWebBrowser/.*\\.html
GoWebBrowser/.*\\.png'''
        }
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
  }
  steps{
    shell('''#!/bin/bash -x
cd GoWebServer
sudo docker build -t http-app .
sudo docker rm -f testing-app
cid=$(sudo docker run -d --name testing-app -p 8001:8000  http-app)
echo "cid=$cid" > ../props.env
cip=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' ${cid})
sudo docker run --rm siege-engine -g http://$cip:8000/
[ $? -ne 0 ] && exit 1
sudo docker kill ${cid}
sudo docker rm ${cid}''')
  }
}

job('test-browser') {
  logRotator( -1, 5 ,-1 ,-1 )
  triggers {
    upstream('build-browser', 'UNSTABLE')
  }
  steps {
    shell('''#!/bin/bash -x
sudo docker kill testing-app
cid=$(sudo docker run -d --name testing-app -p 8000:8000  http-app)
echo "cid=$cid" > props.env''')
    environmentVariables {
      propertiesFile('props.env')
    }
    shell('''#!/bin/bash -x
cip=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' ${cid})
sudo docker run --rm siege-engine http://$cip:8000/ > output''')

    shell('''#!/bin/bash
avail=$(cat output | grep Availability)
echo $avail
if [[ "$avail" == *"100.00"* ]]
then
\techo "Availability high enough"
\texit 0
else
\techo "Availability too low"
\texit 1
fi''')
    groovyCommand '''def columns = ["Transactions","Elapsed time","Data transferred","Response time","Transaction rate","Throughput","Concurrency"]


def input = new File('output')
def map = new LinkedHashMap<String, String>()

input.eachLine { line ->
columns.each { column ->
    if(line.startsWith(column)){
        def val = (line =~ /[\\w ]*:[\\s]*([\\d.]*)[ \\n]?.*/)
        if(val.matches())
            map.put(column, val.group(1))
        else
            println "Failed for " + line
    }
}
}

def output = new File('output.csv')
if(output.exists())
output.delete()
map.keySet().each {output.append(it+",")}
output.append("\\n")
map.values().each {output.append(it+",")}
'''
  shell('''sudo docker kill ${cid}
sudo docker rm ${cid}''')
  }

  publishers {
    plotBuildData {
        plot('siege_data', 'output.csv') {
            title('Siege results')
            numberOfBuilds(10)
            csvFile('output.csv') {
                includeColumns('Response time')
                showTable()
            }
        }
    }
  }
}


job('release-browser') {
  logRotator( -1, 5 ,-1 ,-1 )
  triggers {
        upstream('test-browser', 'UNSTABLE')
    }
}
