# 3.1. Installing MySQL Server

#### Linux

##### installing MySQL Server 5.7

1. To install MySQL Server 5.7, open the terminal and type the commands:

```sh
sudo apt update
sudo apt install mysql-server
```

2. If you want to configure the user in MySQL simply, just run the command:

```sh
mysql_secure_installation
```

##### installing MySQL Server 8

1. To install version 8.x, you need to do a few more procedures.
   Go to the / tmp folder and download the .deb file as shown below so we can add the repository needed to install MySQL Server 8:
   
```sh
$ cd /tmp
$ curl -OL https://dev.mysql.com/get/mysql-apt-config_0.8.10-1_all.deb

# After that, we will install the .deb file like this:
$ sudo dpkg -i mysql-apt-config*

# After adding the repository, when prompted, press the ENTER key and then run the command below to update the repository list:
$ sudo apt update

# After that you will be able to install MySQL Server 8 as normal as follows:
$ sudo apt install mysql-server

```


#### MacOS

Open the terminal and install Homebrew (a package manager for Mac) if you have not already installed:
```
$ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/instal
```

Run the command below to install MySQL Server 5.7 using Homebrew:

```sh
$ brew install mysql@5.7 Inicie o serviço do MySQL Server:
$ brew services start mysql@5.7
```

To install MySQL Server 8 using Homebrew, run the command:

```
$ brew install mysql
```
And to start the MySql was a service:

```sh
$ brew services start mysql
```
