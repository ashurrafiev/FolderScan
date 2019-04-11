# FolderScan

**FolderScan** is a tool for quick scanning for changed files within the speficied folders, comparing the current structure to a previously created snapshot. The tool uses modification date/time and file size information provided by the system to detect modifications, it does not calculate file hashes.

The list of directories to scan are specified in a config file. The directories are scanned recursively. [Example](example.cfg) config file:

```
D:/Workspace

ignore:
*/.git
*/.metadata
*/.recommenders
*/bin
```

To use it, rename to `folderscan.cfg`. In this example, the tool will scan `D:/Workspace` ignoring folders named `.git`, `.metadata`, etc. _Please note, that the ignore filter is only applied to folders, not files!_

## Download

**Jar file:** [folderscan.jar](https://github.com/ashurrafiev/FolderScan/releases/download/1.0.1/folderscan.jar) (7.24 KB)

## Using the tool

To recursively scan directories and save the snapshot as `folderscan.data` use:

```
java -jar folderscan.jar -save
```

For the given example config, the output will probably be:

```
Loading config ...
Loading data ...
.\folderscan.data (The system cannot find the file specified)
Scanning D:/Workspace ...
Saving data ...
Data saved
Comparing ...
No saved data for D:/Workspace
Done
```

To recursively scan directories and compare with the previously saved snapshot use:

```
java -jar folderscan.jar
```

Assuming, some files have been modified, the output will be:

```
Loading config ...
Loading data ...
Looking for modifications since 2019-04-01
Scanning D:/Workspace ...
Comparing ...
In D:/Workspace/FolderScan/src/com/xrbpowered/folderscan:
   Modified on 2019-04-02: FolderScan.java
In D:/Workspace/FolderScan:
   Added: folderscan.data
Done
```

If you expect many file modifications, it may be useful to redirect the output to a text file:

```
java -jar folderscan.jar > folderscan.txt
```

You can store `folderscan.data` snapshot together with the backup and then use **-in** option to tell which snapshot to compare to:

```
java -jar folderscan.jar -in mybackup/folderscan.data
```

## Command Line Options Summary

| option | description |
| :--- | :--- |
| **-save** | Save snapshot of the folder structure to `folderscan.data`. |
| **-in**&nbsp;filename | Read snapshot data from a specific file. By default, the data is read from `folderscan.data` in the current directory. |
