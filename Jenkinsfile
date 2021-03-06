def GIT_REPOSITORY_REPO = "http://gitlab.gec.io/gecscf/commons-file-importer.git"
pipeline {
  agent { node { label 'gecscf-unix-001' } }
  triggers { pollSCM('H/3 * * * *') }
  stages {
    stage('[SCM] Checkout Common file importer service') {
      steps {
        git branch: '${git_branch}', credentialsId: '28413f37-4882-46c8-9b30-6530cc145bed', url: GIT_REPOSITORY_REPO
        script {
            GIT_COMMIT_EMAIL = sh (
                script: 'git --no-pager show -s --format=\'%ae\'',
                returnStdout: true
            ).trim()
        }
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
    success {
        sh "mvn sonar:sonar -Dsonar.host.url=http://${sonar_host}:9000 -Dsonar.junit.reportPaths=target/surefire-reports -Dsonar.analysis.buildNumber=${BUILD_NUMBER} -Dsonar.analysis.author=${GIT_COMMIT_EMAIL}"
    }
  }
}