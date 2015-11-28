job('tag-version'){
  logRotator( -1, 1 ,-1 ,-1 )
  scm {
    git {
      remote {
        name('origin')
        url('https://github.com/drBosse/dayofdocker15.git')
      }
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
