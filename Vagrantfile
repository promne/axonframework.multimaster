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
	config.vm.box_check_update = false  

	config.vbguest.auto_update = false		
		
		
	#vagrant-cachier
	config.cache.auto_detect = true
	#config.cache.scope = :box	  

  
	(1..1).each do |i|  
		config.vm.define "axonmulti-#{i}", primary: true  do |node|
	
			node.vm.hostname = "axonmulti-#{i}"
			node.vm.provider :virtualbox do |vb|
			  vb.memory = 512
			  vb.cpus = 4
			  vb.name = "axonmulti-#{i}"
			end		
			
			node.vm.network :private_network, ip: "10.128.75.#{100+i}", netmask: "255.255.0.0"
			node.vm.network :private_network, ip: "192.168.56.#{100+i}", netmask: "255.255.255.0"
				
			node.vm.provision :ansible_local do |ansible|
				#ansible.galaxy_role_file = "ansible/requirements.yml"
				ansible.galaxy_command = "sudo ansible-galaxy install --role-file=%{role_file}" # download as local role, only once
				
				ansible.verbose = "vv"
				ansible.sudo = true
				ansible.playbook = "ansible/site.yml"
				ansible.groups = {
					"mongodb" => ["axonmulti-#{i}"],
				}		
			end
		end
	end	
end
