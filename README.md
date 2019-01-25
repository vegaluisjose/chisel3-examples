# Setup

* Install Python
```
sudo apt install -y python
```

* Install sbt (Debian) [source](https://www.scala-sbt.org/release/docs/Installing-sbt-on-Linux.html)
```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update
sudo apt-get install sbt
```

* Install sbt (CentOS) [source](https://www.scala-sbt.org/1.0/docs/Installing-sbt-on-Linux.html)
```
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt
```

* Install sbt (OSX)
```
brew install sbt
```

* Install [Verilator](https://www.veripool.org/wiki/verilator)
```
sudo apt-get install git make autoconf g++ flex bison
git clone http://git.veripool.org/git/verilator
cd verilator
git checkout v4.008 -b v4.008
autoconf
./configure
make
sudo make install
```

* OSX: `brew install verilator` (older versions)
* Linux: `sudo apt install verilator` (older versions)

* Sources:
* [Installing Verilator official documentation](https://www.veripool.org/projects/verilator/wiki/Installing)
* [Verilator Manual](https://www.veripool.org/projects/verilator/wiki/Manual-verilator)
