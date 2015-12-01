// Your github username
// It should come as a parameter to this seed job
//GITHUB_USERNAME="githubusername"

// Variable re-used in the jobs
PROJ_NAME="webserver"
REPO_URL="https://github.com/${GITHUB_USERNAME}/dayofdocker15.git"

// Configured to use to define the build pipeline as well
FIRST_JOB_NAME="1.build-${PROJ_NAME}_GEN"

job("${FIRST_JOB_NAME}") {
  logRotator( -1, 5 ,-1 ,-1 )
  scm {
    git {
      remote {
        name('origin')
        url("${REPO_URL}")
      }
      branch('master')
      configure {
        it / 'extensions' << 'hudson.plugins.git.extensions.impl.PathRestriction' {
          'includedRegions' '''GoWebBrowser/.*\\.go
GoWebBrowser/.*\\.html
GoWebBrowser/.*\\.png
version\\.txt'''
        }
      }
    }
  }
  properties {
    environmentVariables {
      keepSystemVariables(true)
      keepBuildVariables(true)
      env('GITHUB_USERNAME', "${GITHUB_USERNAME}")
    }
  }
  triggers {
    scm('* * * * *')
  }
  steps{
    shell('''#!/bin/bash -x
echo "version=\$(cat version.txt)" > props.env

cd GoWebServer
imageid=$(sudo docker build -q -t ${GITHUB_USERNAME}/http-app:snapshot . 2>/dev/null | awk '/Successfully built/{print $NF}')

sudo docker rm -f testing-app
cid=$(sudo docker run -d --name testing-app -p 8001:8000 ${GITHUB_USERNAME}/http-app:snapshot)
echo "cid=$cid" >> ../props.env
echo "IMAGEID=$imageid" >> ../props.env
cip=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' ${cid})
sudo docker run --rm siege-engine -g http://$cip:8000/
[ $? -ne 0 ] && exit 1
sudo docker kill ${cid}
sudo docker rm ${cid}''')
  }
  publishers {
    downstreamParameterized {
      trigger("2.test-${PROJ_NAME}_GEN") {
        condition('SUCCESS')
        parameters{
          predefinedProp('GITHUB_USERNAME', '${GITHUB_USERNAME}')
          gitRevision(false)
          propertiesFile('props.env', failTriggerOnMissing = true)
        }
      }
    }
  }
}



job("2.test-${PROJ_NAME}_GEN") {
  logRotator( -1, 40 ,-1 ,-1 )
  steps {
    shell('''#!/bin/bash -x
sudo docker rm -f testing-app
testing_cid=$(sudo docker run -d --name testing-app -p 8000:8000  $IMAGEID)
echo "testing_cid=$testing_cid" > props.env
''')
    environmentVariables {
      propertiesFile('props.env')
    }
    shell('''#!/bin/bash -x
cip=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' ${testing_cid})
sudo docker run --rm siege-engine http://$cip:8000/ > output 2>&1
''')
    shell('''#!/bin/bash
avail=$(cat output | grep Availability)
echo $avail
if [[ "$avail" == *"100.00"* ]]
then
	echo "Availability high enough"
    sudo docker tag $IMAGEID ${GITHUB_USERNAME}/http-app:stable
	exit 0
else
	echo "Availability too low"
	exit 1
fi
''')
    shell('''#We want a groovy script for parsing siege output into CSV
#As this job is not actually pulling code from git, we are just creating the groovy script here to make life easier.

#Remove any old version
rm -f parse.groovy

#Cat the whole groovy script to a file
cat <<EOT >> parse.groovy
def columns = ["Transactions","Elapsed time","Data transferred","Response time","Transaction rate","Throughput","Concurrency"]


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
EOT
''')
    shell('''
echo "Run parse.groovy with docker"
ls -al
pwd -P
rm -f output.csv
echo "Running /source/parse.groovy"
sudo docker run -t --rm -v /opt/containers/jenkins_home/jobs/${JOB_NAME}/workspace:/source webratio/groovy parse.groovy
cat output.csv
''')
  }
  publishers {
    plotBuildData {
      plot('siege_data', 'output.csv') {
        title('Siege results')
        logarithmic()
        csvFile('output.csv') {
          includeColumns('Transaction rate,Availability')
        }
      }
    }
    downstreamParameterized {
      trigger("3.release-${PROJ_NAME}_GEN") {
        condition('SUCCESS')
        parameters{
          predefinedProp('VERSION', '${version}')
          predefinedProp('GITHUB_USERNAME', '${GITHUB_USERNAME}')        }
      }
    }
  }
}



job("3.release-${PROJ_NAME}_GEN") {
  logRotator( -1, 5 ,-1 ,-1 )
  steps {
    shell('''#!/bin/bash
sudo docker tag -f ${GITHUB_USERNAME}/http-app:stable ${GITHUB_USERNAME}/http-app:latest 
sudo docker tag -f ${GITHUB_USERNAME}/http-app:stable ${GITHUB_USERNAME}/http-app:$VERSION 
# no git here yet
# sudo docker tag http-app/http-app:$(git describe)
sudo docker rm -f deploy-app
sudo docker run -d --name deploy-app -p 81:8000 ${GITHUB_USERNAME}/http-app:latest
''')
    shell('''
sudo docker ps |grep ${GITHUB_USERNAME}/http-app
sudo docker images |grep ${GITHUB_USERNAME}/http-app
''')
  }
}





listView("${PROJ_NAME}-jobs_GEN") {
  description("All ${PROJ_NAME} project related jobs")
  jobs {
    regex(".*-${PROJ_NAME}.*")
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
    }
}



buildPipelineView("${PROJ_NAME}-pipeline_GEN") {
  title("Project ${PROJ_NAME} CI Pipeline")
  displayedBuilds(50)
  selectedJob("${FIRST_JOB_NAME}")
  alwaysAllowManualTrigger()
  showPipelineParametersInHeaders()
  showPipelineParameters()
  showPipelineDefinitionHeader()
  refreshFrequency(60)
}
