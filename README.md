# TODO

- Make app generic. JSON file for payload. Config item for default URL

## Steps

- Created vertx server
- Add Vue just in an index.html file
- Took out css into its own file
- Creating Vue front 
```
npm create vite@latest frontend -- --template vue
Need to install the following packages:
create-vite@8.0.1
Ok to proceed? (y) y
```


- Build gradle locally 
```
echo "org.gradle.java.home=/home/$USER/Code/buck-all/opt/jdk21" >> gradle.properties
./gradlew build
```

- Build container
```
docker build -t load-generator .

docker run -it -p 10200:10200 load-generator
```

