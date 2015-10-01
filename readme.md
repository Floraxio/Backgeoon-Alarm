Starter template for phonegap/cordova plugins
=====================================

**What?**
A starter template for (android) cordova plugins.

**Why?**
Because the docs is to verbose. You just at minimum theese 3 files and your favorite editor. No need for creating new eclipse projects
or hacking generated code.



Usage
==========

 1. Download the files.
 2. Copy to a folder, for example `X:\my-custom-cordova-plugin\`
 3. In your phonegap/cordova app folder run `cordova plugin add "X:\my-custom-cordova-plugin\"`.
 4. `cordova build`
 5. Done. You can test that the plugin works by running this in your javascript file:

 	```
    	hello.world("my own custom plugin :D", function(response) {
        	console.log(response); //Should return "my own custom plugin :D"
    	});
 	```

TODO
=====
 
 - Better example
 - iOS example


Requirements
============

 - cordova or phonegap CLI tools (install with `npm cordova -g`)
 - android sdk etc