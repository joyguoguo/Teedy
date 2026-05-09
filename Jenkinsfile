def runMaven(String goals) {
    def mvnCommand = "mvn -B -Dmaven.repo.local=.m2/repository ${goals}"
    if (isUnix()) {
        sh mvnCommand
    } else {
        bat mvnCommand
    }
}

pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Clean') {
            steps {
                script {
                    runMaven('clean')
                }
            }
        }

        stage('Compile') {
            steps {
                script {
                    runMaven('compile')
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    runMaven('test -Dmaven.test.failure.ignore=true')
                }
            }
        }

        stage('PMD') {
            steps {
                script {
                    runMaven('pmd:pmd')
                }
            }
        }

        stage('JaCoCo') {
            steps {
                script {
                    runMaven('jacoco:report')
                }
            }
        }

        stage('Site') {
            steps {
                script {
                    runMaven('site')
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    runMaven('package -DskipTests')
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true

            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true, allowEmptyArchive: true

            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site',
                reportFiles: 'index.html',
                reportName: 'Maven Site'
            ])
        }
    }
}
