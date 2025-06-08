# HBadgerStation

An application for managing files for 3D printing. Early alpha version

## Manual

The assumption is that files are kept in projects/directories, with the assumption that each project can have many files and subdirectories.

Example:<br>
```
/minifigure to print<br>
--/minifigure.stl<br>
--/image.jpg<br>
--/presupported/minifigure-sup.stl<br>
```
The /minifigure to print directory should be imported as the basic unit.
When selecting the root option during import, the program will treat the directories in the selected folder as the basic units

Example:
```
/my root<br>
--/minifigure 1<br>
--/minifigure 2<br>
```
when selecting the my root directory for import and selecting the root option, two projects will be imported (minifigure 1 and 2)

## Configuration 

Stored in _config.ini_ 
```
[cache]
cacheOn=1 - image cache 0/1 - off/on
cacheWhenImport=1 - cache images on import (if off and cache is on it will be done when image is displayed) 
cacheDirectory="./cache" - cache directory

[database]
dbPath="library.db" - file to stora data

[file types]
printableFiles="stl,3mf" - extensions for files that can be 3d printed
images="jpg,jpeg,png" - extensions for images
```
Cache should be turned on - it contains scaled images and make javafx work faster

## Running program.
From source _mvn javafx:run_

There is also zip with executable prepared for Windows - this one can be downloaded and run after unpacking. It contains java machine
