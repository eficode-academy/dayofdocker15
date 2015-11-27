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
git tag -a -m "Tag: $(cat version.txt)" $(cat version.txt)
git push --tags''')
  }
}
