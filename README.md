App to benchmark io_uring (splice) vs epoll (sendfile) zero-copy file transfer.

Please replace in pom you linux version in this part

```
        <os.detected.name>linux</os.detected.name>
        <os.detected.arch>aarch_64</os.detected.arch>
        <os.detected.classifier>linux-aarch_64</os.detected.classifier>
```

To enable epoll use 
```
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-native-epoll</artifactId>
            <classifier>YOUR_OS_CLASSIFIER</classifier> 
        </dependency>
```

or to enable io_uring - comment epoll and use 

```
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-classes-io_uring</artifactId>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-native-io_uring</artifactId>
            <classifier>YOUR_OS_CLASSIFIER</classifier>
        </dependency>
```


App can be run in intelij IDEA just as simple Spring app or you can build fat executable jar with **mvn clean install**

To test which transport actully used call

curl 127.0.0.1:8080/thread-name


To retrieve file use

127.0.0.1:8080/file/file_name_from_DATA_folder_of_the_repo
