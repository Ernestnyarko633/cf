/** Pipeline Script for deploying Neobank Customer to Kubernetes Cluster
* @Author Peter Yefi
* @Created November 17, 2023
**/

node {
    def app

    try {
        stage('Clone repository') {
            /* Let's make sure we have the repository cloned to our workspace */
            checkout scm
            checkout scm
            sh 'cp ./src/main/resources/applications.properties.account ./src/main/resources'
            sh 'mv ./src/main/resources/applications.properties.account ./src/main/resources/application.properties'

            
            /* sh 'cp ./src/main/resources/application.properties.account ./src/main/resources'
            /* sh 'mv ./src/main/resources/application.properties.account ./src/main/resources/application.properties'


            if(env.BRANCH_NAME == 'develop') {
                sh ('sed -i \'s|SERVER_BASE_URL|https://apis-neobank-account-staging.completefarmer.com|\' src/main/resources/application.properties')
                sh ('sed -i \'s|ENV|staging|\' src/main/resources/application.properties')

                withCredentials([
                    string(credentialsId: 'neobank-db-instance', variable: 'DB_HOST'),
                    string(credentialsId: 'neobank-account-db-user-staging', variable: 'DB_USER'),
                    string(credentialsId: 'neobank-account-db-pass-staging', variable: 'DB_PASS'),
                    string(credentialsId: 'neobank-account-db-name-staging', variable: 'DB_NAME'),
                    string(credentialsId: 'neobank-db-port', variable: 'DB_PORT'),
                    string(credentialsId: 'gateway-service-url-staging', variable: 'GATEWAY_URL')
                ]) {
                    sh ('sed -i "s|DB_HOST|jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}|" src/main/resources/application.properties')
                    sh ('sed -i "s|DB_PASS|${DB_PASS}|" src/main/resources/application.properties')
                    sh ('sed -i "s|DB_USER|${DB_USER}|" src/main/resources/application.properties')
                    sh ('sed -i "s|GATEWAY_URL|${GATEWAY_URL}|" src/main/resources/application.properties')
                }
            } else if(env.BRANCH_NAME == 'main') {
                 sh ('sed -i \'s|SERVER_BASE_URL|https://apis-neobank-account.completefarmer.com|\' src/main/resources/application.properties') 
                 sh ('sed -i \'s|ENV|production|\' src/main/resources/application.properties') 
                 withCredentials([
                    string(credentialsId: 'neobank-db-instance', variable: 'DB_HOST'),
                    string(credentialsId: 'neobank-account-db-user-prod', variable: 'DB_USER'),
                    string(credentialsId: 'neobank-account-db-pass-prod', variable: 'DB_PASS'),
                    string(credentialsId: 'neobank-account-db-name-prod', variable: 'DB_NAME'),
                    string(credentialsId: 'neobank-db-port', variable: 'DB_PORT'),
                    string(credentialsId: 'gateway-service-url-prod', variable: 'GATEWAY_URL')
                ]) {
                    sh ('sed -i "s|DB_HOST|jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}|" src/main/resources/application.properties')
                    sh ('sed -i "s|DB_USER|${DB_USER}|" src/main/resources/application.properties')
                    sh ('sed -i "s|DB_PASS|${DB_PASS}|" src/main/resources/application.properties')
                    sh ('sed -i "s|GATEWAY_URL|${GATEWAY_URL}|" src/main/resources/application.properties')
                }
            }
        }   

        stage('Build image') {
            /**
            * Choose deployment environment variable for run command in dockerfile
            * based on branch triggering the build process
            */
            lock('Environment Tagging') {
                /* This builds the actual image; synonymous to
                * docker build on the command line */
                withCredentials([string(credentialsId: 'neobank-ecr-repo', variable: 'NEOBANK_ECR_REPO'), 
                string(credentialsId: 'neobank-sonarqube-analysis', variable: 'NEOBANK_SONARQUBE_ANALYSIS'),
                string(credentialsId: 'neobank-account-sentry-token', variable: 'SENTRY_TOKEN')]) {
                    // update pom.xml with sentry token
                    sh ('sed -i "s|ACCOUNT_SENTRY_AUTH_TOKEN|${SENTRY_TOKEN}|" pom.xml')
                    app = docker.build("${NEOBANK_ECR_REPO}", "--build-arg NEOBANK_SONARQUBE_ANALYSIS=${NEOBANK_SONARQUBE_ANALYSIS} .")
                }
            } 
        }
        
        if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop' ) {
            stage('Push Image') {
                /* Finally, we'll push the image with tag of the current build number
                * Pushing multiple tags is cheap, as all the layers are reused. */
                lock('ImagePush') {
                    withCredentials([
                        string(credentialsId: 'neobank-registry', variable: 'NEOBANK_REGISTRY'), 
                        string(credentialsId: 'neobank-registry-url', variable: 'NEOBANK_REGISTRY_URL'),
                        string(credentialsId: 'neobank-ecr-repo-cred', variable: 'NEOBANK_ECR_REPO_CRED')
                    ]) {
                        def tag = ''
                        if (env.BRANCH_NAME == 'main'){
                            tag = 'cf-neobank-account-prod-latest'
                            sh ('sed -i "s|IMAGE_TAG|cf-neobank-account-prod-latest|" src/cf-helm/values.yaml')
                        } else if (env.BRANCH_NAME == 'develop'){
                            tag = 'cf-neobank-account-stage-latest'
                            sh ('sed -i "s|IMAGE_TAG|cf-neobank-account-stage-latest|" src/cf-helm/values.yaml')
                        }
                        sh ('sed -i "s|CON_REGISTRY|${NEOBANK_REGISTRY}|" src/cf-helm/values.yaml')

                        docker.withRegistry("${NEOBANK_REGISTRY_URL}", "${NEOBANK_ECR_REPO_CRED}") {
                            app.push(tag)
                        }
                    }
                }
            }

            def deploy_title = ''
            def ns = ''
            def url = ''
            charts = ''
            switch(env.BRANCH_NAME) {
                case 'develop':
                    deploy_title = 'Staging'
                    ns = 'staging'
                    url = "https://apis-neobank-account-staging.completefarmer.com"
                break
                case 'main':
                    deploy_title = 'Production'
                    ns = 'production'
                    url = "https://apis-neobank-account.completefarmer.com"
                break
            }

            stage("Deploy To ${deploy_title} Environment") {
                /**
                * Deploy to production or staging environment when the job is 
                * triggered by either master or dev branch
                */
                withCredentials([string(credentialsId: 'neobank-context', variable: 'NEOBANK_CONTEXT')]) {
                    sh 'kubectl config use-context ${NEOBANK_CONTEXT}'
                    sh 'helm lint ./src/cf-helm/'
                    sh "helm upgrade --install --wait --timeout 360s --force cf-neobank-api-account-service src/cf-helm -n=${ns}"
                    // slackSend(color: 'good', message: "Successfully deployed Neobank Account Service at ${url}")
                    // office365ConnectorSend webhookUrl: "${env.TEAM_WEBHOOK}", status: 'Success', message: "Neobank Account Service deployed at ${url}"
                }
            }
        }
    } catch(err) {
        // slackSend(color: '#F01717', message: "${err}")
        // office365ConnectorSend webhookUrl: "${env.TEAM_WEBHOOK}", message: "${err}"
        error "Build Failed ${err}"
    } finally {
        if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'main'){
            def envName = env.BRANCH_NAME == 'develop' ? 'staging' : 'production'
            if (currentBuild.currentResult == 'SUCCESS'){
                // jiraSendDeploymentInfo environmentId: "${envName}", environmentName: "${envName}", environmentType: "${envName}", state: "successful"
            }
        }
        // Remove dangling images
        sh 'docker system prune -f'
    }
}
