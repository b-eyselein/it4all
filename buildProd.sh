mkdir -p public

# client production build
cd react-ui || exit

npm i

npm run build

cp -r build/* ../public

cd .. || exit

# TODO: server production build...
sbt packageZipTarball
