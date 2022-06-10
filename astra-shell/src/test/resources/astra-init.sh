#!/usr/bin/env bash

# set env vars if not set
if [ -z "$ASTRA_VERSION" ]; then
	export SASTRA_VERSION="0.3.1"
fi

if [ -z "$ASTRADIR" ]; then
	export ASTRA_DIR="$HOME/.astra"
fi

if [ -f "${ASTRA_DIR}/etc/config" ]; then
	source "${ASTRA_DIR}/etc/config"
fi

# infer platform
function infer_platform() {
	local kernel
	local machine
	
	kernel="$(uname -s)"
	machine="$(uname -m)"
	
	case $kernel in
	Linux)
	  case $machine in
	  i686)
		echo "LinuxX32"
		;;
	  x86_64)
		echo "LinuxX64"
		;;
	  armv6l)
		echo "LinuxARM32SF"
		;;
	  armv7l)
		echo "LinuxARM32HF"
		;;
	  armv8l)
		echo "LinuxARM32HF"
		;;
	  aarch64)
		echo "LinuxARM64"
		;;
	  *)
	  	echo "LinuxX64"
	  	;;
	  esac
	  ;;
	Darwin)
	  case $machine in
	  x86_64)
		echo "DarwinX64"
		;;
	  arm64)
		if [[ "$sstra_rosetta2_compatible" == 'true' ]]; then
			echo "DarwinX64"
		else
			echo "DarwinARM64"
		fi
		;;
	  *)
	  	echo "DarwinX64"
	  	;;
	  esac
	  ;;
	*)
	  echo "$kernel"
	esac
}

ASTRA_PLATFORM="$(infer_platform | tr '[:upper:]' '[:lower:]')"
export ASTRA_PLATFORM

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
solaris=false
freebsd=false
ASTRA_KERNEL="$(uname -s)"
case "${ASTRA_KERNEL}" in
	CYGWIN*)
		cygwin=true
		;;
	Darwin*)
		darwin=true
		;;
	SunOS*)
		solaris=true
		;;
	FreeBSD*)
		freebsd=true
esac

# Determine shell
zsh_shell=false
bash_shell=false

if [[ -n "$ZSH_VERSION" ]]; then
	zsh_shell=true
elif [[ -n "$BASH_VERSION" ]]; then
	bash_shell=true
fi


OLD_IFS="$IFS"
IFS=$'\n'
scripts=($(find "${ASTRA_DIR}/src" "${ASTRA_DIR}/ext" -type f -name 'astra-*.sh'))
for f in "${scripts[@]}"; do
	source "$f"
done
IFS="$OLD_IFS"
unset OLD_IFS scripts f

# Create upgrade delay file if it doesn't exist
if [[ ! -f "${ASTRA_DIR}/var/delay_upgrade" ]]; then
	touch "${ASTRA_DIR}/var/delay_upgrade"
fi

# set curl connect-timeout and max-time
if [[ -z "$astra_curl_connect_timeout" ]]; then astra_curl_connect_timeout=7; fi
if [[ -z "$astracurl_max_time" ]]; then astra_curl_max_time=10; fi

# set curl retry
if [[ -z "${astra_curl_retry}" ]]; then astra_curl_retry=0; fi

# set curl retry max time in seconds
if [[ -z "${astra_curl_retry_max_time}" ]]; then astra_curl_retry_max_time=60; fi

# set curl to continue downloading automatically
if [[ -z "${astra_curl_continue}" ]]; then astra_curl_continue=true; fi

# read list of candidates and set array

# source completion scripts
if [[ "$astra_auto_complete" == 'true' ]]; then
	if [[ "$zsh_shell" == 'true' ]]; then
		# initialize zsh completions (if not already done)
		if ! (( $+functions[compdef] )) ; then
			autoload -Uz compinit
			if [[ $ZSH_DISABLE_COMPFIX == 'true' ]]; then
				compinit -u -C
			else
				compinit
			fi
		fi
		autoload -U bashcompinit
		bashcompinit
		source "${ASTRADIR}/contrib/completion/bash/sdk"
		__astra_echo_debug "ZSH completion script loaded..."
	elif [[ "$bash_shell" == 'true' ]]; then
		source "${ASTRA_DIR}/contrib/completion/bash/sdk"
		__astra_echo_debug "Bash completion script loaded..."
	else
		__astra_echo_debug "No completion scripts found for $SHELL"
	fi
fi

