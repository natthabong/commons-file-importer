def GIT_REPOSITORY_REPO = "http://gitlab.gec.co.th/gecscf/commons-file-importer.git"
pipeline {
  agent { node { label 'gecscf-unix-001' } }
  triggers { pollSCM('H/3 * * * *') }
  stages {
    stage('[SCM] Checkout Common file importer service') {
      steps {
        git branch: 'dev', credentialsId: '28413f37-4882-46c8-9b30-6530cc145bed', url: GIT_REPOSITORY_REPO
      }
    }
    stage('[MAVEN] Pack sources') {
      steps {
        sh 'mvn clean install'
      }
    }
  }
  post { 
    always { 
       junit 'target/surefire-reports/*.xml'
    }
  }
}