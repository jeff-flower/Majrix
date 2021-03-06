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
