#!/usr/bin/env groovy
@Library('jenkinsPipelineSharedLibrary')

import com.tieto.fs.jenkins.pipeline.steps.utility.*

final String REGISTRY_URL = "https://fs-pcm-docker.artifactory.paymentscommon.pub.tds.tieto.com"
final String REGISTRY_CREDENTIALS = "artifactory-uploader"

def stages = {
    return {
        stage('Build') {
            docker.withRegistry(REGISTRY_URL, REGISTRY_CREDENTIALS) {
                try {
                    mvn(this, 'clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Psnapshots -U')
                } catch (error) {
                    throw error
                }
            }
        }
    }
}

basicPipeline {
    mavenProject = 'axis1'
    gitProject = mavenProject
    project = mavenProject
    owasp_check = false
    disableSonarQubeStage = true
    customizedPipeline = stages()
    skip_stages_list = 'Dependency Track'
}
