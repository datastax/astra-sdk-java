#!/bin/bash

set -e

track_last_command() {
    last_command=$current_command
    current_command=$BASH_COMMAND
}
trap track_last_command DEBUG

echo_failed_command() {
    local exit_code="$?"
	if [[ "$exit_code" != "0" ]]; then
		echo "'$last_command': command failed with exit code $exit_code."
	fi
}
trap echo_failed_command EXIT


# Global variables
ASTRA_VERSION="5.15.0"
ASTRA_PLATFORM=$(uname)

if [ -z "$ASTRA_DIR" ]; then
    ASTRA_DIR="$HOME/.astra"
    ASTRA_DIR_RAW='$HOME/.astra'
else
    ASTRA_DIR_RAW="$ASTRA_DIR"
fi

# Local variables
astra_tmp_folder="${ASTRA_DIR}/tmp"
astra_zip_file="${astra_tmp_folder}/astra-cli-${ASTRA_VERSION}.zip"
astra_zip_base_folder="${astra_tmp_folder}/astra-cli-${ASTRA_VERSION}"

astra_config_file="${ASTRA_DIR}/config"
astra_bash_profile="${HOME}/.bash_profile"
astra_profile="${HOME}/.profile"
astra_bashrc="${HOME}/.bashrc"
astra_zshrc="${ZDOTDIR:-${HOME}}/.zshrc"

astra_init_snippet=$( cat << EOF
#THIS MUST BE AT THE END OF THE FILE FOR ASTRA TO WORK!!!
export ASTRADIR="$ASTRADIR_RAW"
[[ -s "${ASTRA_DIR_RAW}/bin/astra-init.sh" ]] && source "${ASTRA_DIR_RAW}/bin/astra-init.sh"
EOF
)

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
solaris=false;
freebsd=false;
case "$(uname)" in
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

# Sanity checks

echo "Looking for a previous installation of SDKMAN..."
if [ -d "$ASTRA_DIR" ]; then
	echo "ASTRA-CLI found."
	echo ""
	echo "======================================================================================================"
	echo " You already have ASTRA-CLI installed."
	echo " ASTRA-CLI was found at:"
	echo ""
	echo "    ${ASTRA_DIR}"
	echo ""
	echo " Please delete this folder if you need to upgrade."
	echo "======================================================================================================"
	echo ""
	exit 0
fi

echo "Looking for unzip..."
if ! command -v unzip > /dev/null; then
	echo "Not found."
	echo "======================================================================================================"
	echo " Please install unzip on your system using your favourite package manager."
	echo ""
	echo " Restart after installing unzip."
	echo "======================================================================================================"
	echo ""
	exit 1
fi

echo "Looking for zip..."
if ! command -v zip > /dev/null; then
	echo "Not found."
	echo "======================================================================================================"
	echo " Please install zip on your system using your favourite package manager."
	echo ""
	echo " Restart after installing zip."
	echo "======================================================================================================"
	echo ""
	exit 1
fi

echo "Looking for curl..."
if ! command -v curl > /dev/null; then
	echo "Not found."
	echo ""
	echo "======================================================================================================"
	echo " Please install curl on your system using your favourite package manager."
	echo ""
	echo " Restart after installing curl."
	echo "======================================================================================================"
	echo ""
	exit 1
fi

if [[ "$solaris" == true ]]; then
	echo "Looking for gsed..."
	if [ -z $(which gsed) ]; then
		echo "Not found."
		echo ""
		echo "======================================================================================================"
		echo " Please install gsed on your solaris system."
		echo ""
		echo " SDKMAN uses gsed extensively."
		echo ""
		echo " Restart after installing gsed."
		echo "======================================================================================================"
		echo ""
		exit 1
	fi
else
	echo "Looking for sed..."
	if [ -z $(command -v sed) ]; then
		echo "Not found."
		echo ""
		echo "======================================================================================================"
		echo " Please install sed on your system using your favourite package manager."
		echo ""
		echo " Restart after installing sed."
		echo "======================================================================================================"
		echo ""
		exit 1
	fi
fi

echo "Installing ASTRA-CLI scripts..."


# Create directory structure

echo "Create distribution directories..."
mkdir -p "$astra_tmp_folder"

# script cli distribution
echo "Installing script cli archive..."
# fetch distribution
download_url="${ASTRA_SERVICE}/...."
astra_zip_file="${astra_tmp_folder}/sdkman-${ASTRAVERSION}.zip"
astra_zip_base_folder="${astra_tmp_folder}/sdkman-${ASTRA)VERSION}"
echo "* Downloading..."
curl --fail --location --progress-bar "$download_url" > "$astra_zip_file"

# check integrity
echo "* Checking archive integrity..."
ARCHIVE_OK=$(unzip -qt "$astra_zip_file" | grep 'No errors detected in compressed data')
if [[ -z "$ARCHIVE_OK" ]]; then
	echo "Downloaded zip archive corrupt. Are you connected to the internet?"
	echo ""
	echo "If problems persist, please ask for help on our Slack:"
	echo "* easy sign up: https://slack.sdkman.io/"
	echo "* report on channel: https://sdkman.slack.com/app_redirect?channel=user-issues"
	exit
fi

# extract
echo "* Extracting archive..."
if [[ "$cygwin" == 'true' ]]; then
	astratmp_folder=$(cygpath -w "$astratmp_folder")
	astrazip_file=$(cygpath -w "$astrazip_file")
	astrazip_base_folder=$(cygpath -w "$astrazip_base_folder")
fi
unzip -qo "$astrazip_file" -d "$astratmp_folder"

# copy in place

echo "* Copying archive contents..."
cp -rf "${astrazip_base_folder}/"* "$ASTRA_DIR"

# clean up
echo "* Cleaning up..."
rm -rf "$astra_zip_base_folder"
rm -rf "$astra_zip_file"

echo ""


echo "Set version to $ASTRA_VERSION ..."
echo "$ASTRA_VERSION" > "${ASTRA_DIR}/var/version"


if [[ $darwin == true ]]; then
  touch "$astra_bash_profile"
  echo "Attempt update of login bash profile on OSX..."
  if [[ -z $(grep 'sdkman-init.sh' "$astra_bash_profile") ]]; then
    echo -e "\n$astra_init_snippet" >> "$astra_bash_profile"
    echo "Added astra init snippet to $astra_bash_profile"
  fi
else
  echo "Attempt update of interactive bash profile on regular UNIX..."
  touch "${astra_bashrc}"
  if [[ -z $(grep 'sdkman-init.sh' "$astra_bashrc") ]]; then
      echo -e "\n$astra_init_snippet" >> "$astra_bashrc"
      echo "Added astra init snippet to $astra_bashrc"
  fi
fi

echo "Attempt update of zsh profile..."
touch "$astra_zshrc"
if [[ -z $(grep 'astra-init.sh' "$astra_zshrc") ]]; then
    echo -e "\n$astra_init_snippet" >> "$astra_zshrc"
    echo "Updated existing ${astra_zshrc}"
fi


echo -e "\n\n\nAll done!\n\n"

echo ""
echo "Please open a new terminal, or run the following in the existing one:"
echo ""
echo "    source \"${ASTRA_DIR}/bin/astra-init.sh\""
echo ""
echo "Then issue the following command:"
echo ""
echo "    astra help"
echo ""
echo "Enjoy!!!"