#!/bin/bash

ng build dev

echo "Copying js files"
/bin/cp dist/*.js ../news-server/public/

#echo "Copying css files"
#/bin/cp dist/*.css ../news-server/public/

echo "Copying map files"
/bin/cp dist/*.map ../news-server/public/

echo "Copying assets"
/bin/cp dist/assets/* ../news-server/public/
