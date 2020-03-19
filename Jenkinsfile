pipeline {
   agent any

   tools {
      maven "maven"
   }
   //def selected_url="${params.repo}"
   stages {
       stage('test'){
          steps{
            //git url: '${params.repo}'
            
            //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/vijayyadav1997/NevSuite-Backend.git"]]])
            sh "mvn  test"
          }
      }
      stage('Build') {
         steps {
            sh "mvn -Dmaven.test.failure.ignore=true clean package"
         }
      }
   }
    //post{
    //    always{
    //junit '/target/surefire-reports/*.xml'  
    //}
    //}
}
