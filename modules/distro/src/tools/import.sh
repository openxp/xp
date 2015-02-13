#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} -u USER:PASSWORD -s SOURCE_DIR -t TARGET_REPO_PATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Import nodes from an export into a repository branch

	-?				display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-t TARGET_REPO_PATH			target path for import. Format: <repo-name>:<branch-name>:<node-path>. Sample: 'cms-repo:stage:/content'
	-s SOURCE_DIR			path to exported files
	-h HOSTNAME			hostname, defaults to localhost
	-p PORT				port, defaults to 8080
	-n                  enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} -u USER:PASSWORD -s SOURCE_DIR -t TARGET_REPO_PATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]"
}

PRETTY=""

# Parse arguments
while getopts '?u:h:p:t:s:i:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
			t)
				tflag=1
				TARGETPATH="$OPTARG"
				;;
			s)
				sflag=1
				SOURCE="$OPTARG"
				;;
			i)
				iflag=1
				INCLUDEIDS="$OPTARG"
				;;
			p)
				pflag=1
				PORT="$OPTARG"
				;;
            h)
				hflag=1
				HOST="$OPTARG"
				;;
            n)
                PRETTY="| python -mjson.tool"
                ;;
	  		\?)
		    	show_help >&2
		        exit 1
				;;
	  	esac
	done

shift $(($OPTIND - 1))

if [[ -z $AUTH ]]
then
     usageShort
     exit 1
fi

if [[ -z $TARGETPATH ]]
then
     usageShort
     exit 1
fi

if [[ -z $SOURCE ]]
then
     usageShort
     exit 1
fi

if [[ -z $INCLUDEIDS ]]
then
	INCLUDEIDS=true
fi

if [[ -z $HOST ]]
then
     HOST="localhost"
fi

if [[ -z $PORT ]]
then
     PORT="8080"
fi

JSON="{\"sourceDirectory\": \"$SOURCE\", \"targetRepoPath\": \"$TARGETPATH\", \"importWithIds\": $INCLUDEIDS}"

eval "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/export/import' -d '$JSON' $PRETTY"