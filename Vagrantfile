# -*- mode: ruby -*-
# vi: set ft=ruby :.

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

required_plugins = %w(vagrant-cachier)

plugins_to_install = required_plugins.select { |plugin| not Vagrant.has_plugin? plugin }
if not plugins_to_install.empty?
  puts "Installing plugins: #{plugins_to_install.join(' ')}"
  if system "vagrant plugin install #{plugins_to_install.join(' ')}"
    exec "vagrant #{ARGV.join(' ')}"
  else
    abort "Installation of one or more plugins has failed. Aborting."
  end
end

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "ubuntu/xenial64"
  #  config.vm.box = "ubuntu/trusty64"
  config.vm.box_check_update = false

  config.vbguest.auto_update = false

  #vagrant-cachier
  config.cache.auto_detect = true
  config.cache.scope = :machine

  nodes_count = 2

  (1..nodes_count).each do |i|
    config.vm.define "axonmulti-#{i}", primary: true  do |node|

      node.vm.hostname = "axonmulti-#{i}"
      node.vm.provider :virtualbox do |vb|
        vb.name = "axonmulti-#{i}"
        if i==nodes_count
          vb.memory = 2048
          vb.cpus = 4
        else
          vb.memory = 1024          
          vb.cpus = 2
        end
      end

      node.vm.network :private_network, ip: "10.128.75.#{100+i}", netmask: "255.255.0.0"
      node.vm.network :private_network, ip: "192.168.56.#{100+i}", netmask: "255.255.255.0"

      if i==nodes_count
        ansible_inventory_dir = "ansible/hosts"

        node.vm.provision :shell, name:"Collecting ssh keys for all VMs", privileged: false, path: "copykeys.sh"
        
        node.vm.provision :ansible_local do |ansible|
          ansible.limit = "all"
          ansible.inventory_path = "#{ansible_inventory_dir}/vagrant"
          #ansible.verbose = "vv"
          ansible.playbook = "ansible/site.yml"
        end
        
        # setup the ansible inventory file
        Dir.mkdir(ansible_inventory_dir) unless Dir.exist?(ansible_inventory_dir)
        File.open("#{ansible_inventory_dir}/vagrant" ,'w') do |f|
          (1..nodes_count-1).each { |i| f.write "axonmulti-#{i} ansible_host=10.128.75.#{100+i} ansible_ssh_private_key_file=/tmp/vagrant-ssh/id_rsa_axonmulti-#{i} ansible_ssh_common_args='-o StrictHostKeyChecking=no'\n" }
          f.write "axonmulti-#{nodes_count} ansible_connection=local\n"
          
          f.write "[mongodb]\n"
          (1..nodes_count).each { |i| f.write "axonmulti-#{i}\n" }
          
          f.write "[mongodb:vars]\n"  
          f.write "replica_set_name=jirkatest\n"  

          f.write "[mongodb:children]\n"  
          f.write "mongodb_leader\n"  

          f.write "[mongodb_leader]\n"  
          f.write "axonmulti-#{nodes_count}\n"  

          f.write "[builder]\n"  
          f.write "axonmulti-#{nodes_count}\n"  
          
          f.write "[application]\n"  
          (1..nodes_count).each { |i| f.write "axonmulti-#{i}\n" }

        end        

      end
    end
  end
end
