# Website

**A README file can be described as documentation with guidelines on how to use a project. 
Usually it will have instructions on how to download, install, run and contribute to the project but we've already worked on it which is the main [README](https://github.com/SaptarshiSarkar12/Drifty/blob/master/README.md) file of our [project](https://github.com/SaptarshiSarkar12/Drifty).**

- This section contains information about how the [website of this project](https://saptarshisarkar12.github.io/Drifty/) looks like. You can see instruction videos, 
and lots of images (located in [Resources](https://github.com/SaptarshiSarkar12/Drifty/tree/master/Website/Resources) folder) that are used to make the website.

- The website folder also contains files like HTML, CSS, and JavaScript by which you can make necessary changes in this project's website.

## Docker Deployment

Containerized website uses Nginx server.

### Requirements

* [Docker](https://docs.docker.com/get-docker/) (including [Docker Compose](https://docs.docker.com/compose/install/))

### How to get started

1. Easy! Build the image and run the container:

    ```sh
    docker-compose up -d --build
    ```
    
    You may pull the [latest image of the website from Docker Hub](https://hub.docker.com/r/saptarshisarkar12/drifty-website) by using this command:
    
    ```sh
    docker run --rm -d  -p 80:80/tcp saptarshisarkar12/drifty-website:latest
    ```
2. Navigate to http://localhost:80/ to view the website.

> **Note**  
> Check for errors in the logs if this doesn't work via `docker-compose logs -f`.

### Teardown

Bring down the container:

```sh
docker-compose down
```
