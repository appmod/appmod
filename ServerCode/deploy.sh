#!/bin/bash

# export remote=/var/www/html/AdviseProject
export remote=/home/lingfeng/appmod/svn/ServerCode/AdviseProject
# locale = /Library/WebServer/Documents/html/AdviseProject

scp ./AdviseProject/consent.php lingfeng@10.0.106.28:$remote 
# scp ./AdviseProject/submit_consent.php lingfeng@10.0.106.28:$remote 
# scp ./AdviseProject/db_functions.php lingfeng@10.0.106.28:$remote 

# copy the files into the apache server folder 
# cd /home/lingfeng/appmod/svn/ServerCode/AdviseProject
# cp consent.php /var/www/html/AdviseProject