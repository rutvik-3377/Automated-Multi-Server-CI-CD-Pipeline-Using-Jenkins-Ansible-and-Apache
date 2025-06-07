pipeline {
    agent any
 
    environment {
        // Update these variables as per your setup
        ANSIBLE_SERVER = "root@172.31.12.154"    // Ansible Server IP
        ANSIBLE_IP = "172.31.12.154"
        REMOTE_DIR = "/opt"
        FILE_NAME = "index.html"
        FILE_PATH = "Meet/${FILE_NAME}"         // Adjust this if your Jenkins job name is different
    }
 
    stages {
        stage('Clone from GitHub') {
            steps {
                git branch: 'main', url: 'https://github.com/Meet01234/Ansible.git'
            }
        }
 
        stage('Transfer index.html to Ansible') {
            steps {
                sh """
                rsync -e "ssh -o StrictHostKeyChecking=no" -avh /var/lib/jenkins/workspace/${FILE_PATH} root@${ANSIBLE_IP}:${REMOTE_DIR}/
                """
            }
        }
 
        stage('Run Ansible Playbook') {
            steps {
                sh """
                ssh -o StrictHostKeyChecking=no ${ANSIBLE_SERVER} \\
                'ansible-playbook -i /home/ubuntu/sourcecode/hosts /home/ubuntu/sourcecode/playbook.yml'
                """
            }
        }
    }
 
    post {
        success {
            echo '✅ CI/CD pipeline completed successfully!'
        }
        failure {
            echo '❌ CI/CD pipeline failed. Check the logs above.'
        }
    }
}
