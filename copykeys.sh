#!/bin/bash
set -e

mkdir -p /tmp/vagrant-ssh
find /vagrant/ -name "private_key" -exec sh -c "echo {} | egrep -o 'axonmulti-[0-9]+'" \; | xargs -I % cp /vagrant/.vagrant/machines/%/virtualbox/private_key /tmp/vagrant-ssh/id_rsa_%
chmod 0600 /tmp/vagrant-ssh/*
