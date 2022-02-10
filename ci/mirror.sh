#!/bin/bash

REPO_PATH="${PROJECT_HOME}/ws-cxf-ext/"

cd "${REPO_PATH}" && git pull origin master || :
git push github master 
git push pgitlab master
git push bitbucket master
exit 0
