# Majrix
A Clojure implementation of the Matrix client-server API

## What is Matrix?
Since they do it so well, we'll let [these guys](https://www.ruma.io/docs/matrix/) tell you. Go ahead, we'll wait.

The official [Matrix site](https://www.matrix.org) is also a great source of information.

## Um, I think you spelled closure wrong
No, no we didn't. In case you're not already aware of it's awesomeness, [Clojure](https://www.clojure.org), is a functional programming language that runs on the JVM.

[Clojure For the Brave and True](http://www.braveclojure.com) is an excellent resource.

## Getting Started

### IDE Setup
- [Cursive IntelliJ plugin](https://cursive-ide.com/userguide/index.html)
- Emacs setup
  + [general system setup](http://www.braveclojure.com/getting-started/)
  + [emacs setup](www.braveclojure.com/basic-emacs/)
- [Counterclockwise Eclipse plugin](http://doc.ccw-ide.org/documentation.html)
- [Sublime Text](https://spin.atomicobject.com/2016/04/08/sublime-text-clojure/)
  + Note: you'll probably need to set up some things on your own such as Leiningen
  
### Database Setup
Since it's early, we're using a free Graphene Neo4j database instance. Head over to [Graphene]() and sign up. Once you've registered, from the main screen after logging in, go to the 'Connection' tab. Make note of the 'REST URL', the 'REST Username' and the 'REST Password', you'll need them later.
  
### Properties.edn file
You'll need a place to store user specific configuration values. From the root of the project

    touch resources/properties.edn
    
Open the new file and add the following, replacing the values where indicated

    {:database
      {:base-url "your-rest-server-url"
       :username "your-username"
       :password "your-password"}}

### Documentation
We've chosen to use [Marginalia](https://github.com/gdeer81/marginalia) to document our code. Documentation is located in the
'docs' folder. For instructions on how to format comments so the documentation is generated correctly, see [here](gdeer81.github.io/marginalia/).  

To generate documentation, from the project root run `lein marg` This will produce a file named uberdoc.html in the docs folder.

### Testing

We're using [Midje](https://github.com/marick/Midje)

#### Setup
1) In ~/.lein/profiles.clj and add:

    {:user {:plugins [[lein-midje "3.1.3"]]}}

#### [Tutorial](https://github.com/marick/Midje/wiki/A-tutorial-introduction)

#### Running your tests

##### Autotest
Autotest will watch your files and rerun your tests any time you save your files.
To run Autotest from an active repl:

    => (use 'midje.repl)
    => (autotest)

##### Run tests once only
If you just want to run your tests once, from the terminal navigate to the root of
your project and enter:

    => lein midje

### Compojure

### Docker
The `Dockerfile` sets up a development environment for you and lets you focus on
the code rather than setting everything up. This image sets up the following

- Spins up a neo4j instance.
- Downloads leiningen.
- Clones the `majrix` repository (at `/var/lib/majrix`).

You can access `neo4j` (port 7474) and `majrix` (port 3000, must be started
first) server on the host as well!

```sh
docker build -t majrix .
docker create -it --name majrix --publish=7474:7474 --publish=3000:3000 --volume=$HOME/neo4j/data:/data majrix
docker start majrix
docker exec -it majrix bash
```
