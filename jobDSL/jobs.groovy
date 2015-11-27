job('tag-version'){
  logRotator( -1, 1 ,-1 ,-1 )
  scm {
    git {
      remote {
        name('origin')
        url('https://github.com/drBosse/dayofdocker15.git')
      }
    }
  }
  triggers {
    scm('H/5 * * * *')
  }
  steps {
    shell('[ ! -f version.txt ] && exit 1')
    shell('git tag -a -m "Tag: $(cat version.txt)" $(cat version.txt)')
    shell('git push --tags')
  }
}
