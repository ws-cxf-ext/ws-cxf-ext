#!/bin/bash

REPO_PATH="/home/centos/ws-cxf-ext/"

cd "${REPO_PATH}" && git pull origin master || :
git push github master 
exit 
0
