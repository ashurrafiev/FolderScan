# FolderScan

**FolderScan** is a tool for quick scanning for changed files within the speficied folders, comparing the current structure to a previously created snapshot. The tool uses modification date/time and file size information provided by the system to detect modifications, it does not calculate file hashes.

**Added in version 1.1:** difference view in GUI mode.

![Screenshot](screenshot.png?raw=true)

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

**JAR file:** [folderscan.jar](https://github.com/ashurrafiev/FolderScan/releases/download/1.1/folderscan.jar) (241 KB)

## Using the tool

Examples of typical uses, running from command line:

#### Create a snapshot

```
java -jar folderscan.jar -nogui -nocmp -save
```

The program will scan the directories listed in `folderscan.cfg` and save snapshot to `folderscan.data` in the current directory.
**-nocmp** option means that there is no need to load the previous snapshot and do comparison.
**-nogui** option supresses launching GUI window. GUI is needed to view comparison results.

> The snapshot contains file names, sizes, and modification timestamps for all files in subdirectories.
> Contents of the files are not processes or saved. The snapshot is zipped but not encrypted!

#### Scan and compare (default mode)

```
java -jar folderscan.jar
```

> This is the same as double-clicking the JAR file in the file explorer.

The program will scan the directories listed in `folderscan.cfg` and compare to a snapshot previously saved to `folderscan.data`.

#### Save as another snapshot

```
java -jar folderscan.jar -nogui -nocmp -out mysnapshot.data
```

The program will scan the directories listed in `folderscan.cfg` and save snapshot to a specific file (`mysnapshot.data` in this case).
**-nocmp** option means that there is no need to load the previous snapshot and do comparison.
**-nogui** option supresses launching GUI window. GUI is needed to view comparison results.

> The snapshot contains file names, sizes, and modification timestamps for all files in subdirectories.
> Contents of the files are not processes or saved. The snapshot is zipped but not encrypted!

#### Scan and compare to another snapshot

```
java -jar folderscan.jar -in mysnapshot.data
```

The program will scan the directories listed in `folderscan.cfg` and compare to a snapshot previously saved to `mysnapshot.data`.

#### Compare between two snapshots

```
java -jar folderscan.jar -in folderscan.data -alt mysnapshot.data
```

The program will load two specified snapshots (`folderscan.data` and `mysnapshot.data`) and compare them, showing results in the GUI window.
There is no scanning in this case.


## Command Line Options Summary

Usage:

```
java -jar folderscan.jar [options]
```

Options:

| option | description |
| :--- | :--- |
| **-nogui** | Do not open GUI window. |
| **-cfg**&nbsp;_filename_ | Load config from _filename_. If not specified, `folderscan.cfg` will be used. |
| **-d**&nbsp;_path_ | Recursively scan specified directory. Config is ignored. |
| **-out**&nbsp;_filename_ | Write scan snapshot to a file. |
| **-save** | Write scan snapshot to `folderscan.data`. Same as `-out folderscan.data`. |
| **-in**&nbsp;_filename_ | Load snapshot from _filename_.<br/>By default, `folderscan.data` is loaded unless **-nocmp** is specified. |
| **-alt**&nbsp;_filename_ | Load another snapshot from _filename_.<br/>Can be used to compare between two snapshots. |
| **-nocmp** | Do not compare. |
| **-help** | Show command line options and quit. |
