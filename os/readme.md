# Recommended hardware and software
```
OS: Ubuntu 16.04
Storage: at least 120GB SSD
RAM: 12GB
```

# install dependencies
```
sudo apt-get update
sudo apt-get install openjdk-8-jdk
sudo apt-get install git-core gnupg flex bison gperf build-essential zip curl zlib1g-dev gcc-multilib g++-multilib libc6-dev-i386 lib32ncurses5-dev x11proto-core-dev libx11-dev lib32z-dev libgl1-mesa-dev libxml2-utils xsltproc unzip
```

# Download original Android OS source code
Following https://source.android.com/setup/build/downloading

```
mkdir ~/bin
PATH=~/bin:$PATH
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
mkdir android_src
cd android_src
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
repo init -u https://android.googlesource.com/platform/manifest
repo init -u https://android.googlesource.com/platform/manifest -b android-8.1.0_r40	
repo sync
```

# Copy modified code
copy all files in appmod/os/src/art/runtime/*.* to ~/android_src//art/runtime/


# Compile the downloaded source code to build emulator

> make clobber

this will clean up everything

> source build/envsetup.sh

call this command everytime you quite the current terminal

```
lunch aosp-x86_userdebug
make -j8 -> wait for a few hours
make snod
emulator 
```

# install modified OS to pixel 2 xl phone
```
lunch aosp_taimen-userdebug
make -j8
adb reboot boot-loader
fastboot flashall
```

# format of behavior models format
example model stored in appmod/os/sdcard/appmod_models/com.example.duy.caller.txt

```
NS [Number of starting states]
S_1
S_2
...
S_NS
NE [Number of ending states]
E_1
E_2
...
E_NE
M [Number of methods]
M_1
M_2
...
M_M
S [Number of sensitive APIs]
S_1 [0<= S_1: index of the method < M]
S_2 [0<= S_2: index of the method < M]
...
S_S [0<= S_S: index of the method < M]
NT [Number of transitions]
S1  E1  L1 [0<= L1: index of the method < M]
S2  E2  L2 [0<= L2: index of the method < M]
...
ST  ET  LT [0<= LT: index of the method < M]
W [Please refer Section 5 of ISSTA 2018 paper at https://github.com/lebuitienduy/DSM/blob/master/paper/DSM.pdf where W is mentioned]
```

# install behavior model to the phone
adb push appmod/os/sdcard/appmod_models/* /sdcard/appmod_models/


