#!/bin/bash

#required folder script_selenium with selenium scripts
touch error_side
echo "">error_side
echo "_______________________________">error_side

touch datafile
# username for environment
username1=$1
# password for environment - currently we taken only 8 first chars
password1=`echo ${2} | awk '{print substr($0,0,9)}'`
# name environment
environment=$3
# login to bitbucket
usernameBB=$4
#password to bitbucket
passwordBB=$5
echo "_____________${environment}_____________">>error_side
touch scriptSeleniumstart.side
mkdir resultselenium 2>/dev/null

echo "-----start selenium------"
runCurl=10
for file_side in `ls script_selenium`
do

	head -14 script_selenium/$file_side >scriptSeleniumstart.side
	cat login_set>>scriptSeleniumstart.side
	echo '		"value":"'$username1'"'>>scriptSeleniumstart.side
	cat pass_set>>scriptSeleniumstart.side
	echo '		"value":"'$password1'"'>>scriptSeleniumstart.side
	tail -n +51 script_selenium/$file_side>>scriptSeleniumstart.side

	echo "	">>error_side

	namejson=`echo $file_side|sed "s/side//g"`

	selenium-side-runner -c "browserName=chrome" --force scriptSeleniumstart.side  --output-directory=resultselenium --output-format=junit
	err1=`cat resultselenium/${namejson}xml|grep fail|wc -l`

	if [ "${err1}" != "0" ]; then
		echo "  file: $file_side  " >>error_side
		echo "  :::::::::::::::::::::::::  " >>error_side
		sed -n "3p" resultselenium/${namejson}xml|sed "s/\"/\&quot;/g"|sed "s/</  /g"|sed "s/>/  /g"|sed "s/\//\&sol/g">>error_side
		echo "::::::::::::::::::::::::::::::::::::::::::::::" >>error_side
		sed -n "5p" resultselenium/${namejson}xml|sed "s/\"/\&quot;/g"|sed "s/</  /g"|sed "s/>/  /g"|sed "s/\//\&sol/g">>error_side
	echo "     ______________________________     ">>error_side
	runCurl=5
	fi

	echo "file: $file_side"
done

if [ "$runCurl" == "5" ]; then
    data1='{ "body": "'
    data2=`cat  error_side`
    data3='"}'
    alldata=$data1$data2$data3
    echo $alldata>datafile
    touch resultcurl
    curl -D- -u $usernameBB:$passwordBB -X POST --data @datafile -H "Content-Type: application/json;charset=utf-8" https://some.rest.url.fake.sample.pl>resultcurl
fi
echo "-------end selenium-------"
rm -R resultselenium
rm datafile


