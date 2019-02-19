Example config file:

```
D:/Workspace

ignore:
*/.git
*/.metadata
*/.recommenders
*/bin
```

To use it, rename to `folderscan.cfg`.

To recursively scan the listed directories and save the snapshot as `folderscan.data`:

```
>java -jar folderscan.jar -save
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

To recursively scan the listed directories and compare with the previously saved snapshot:

```
>java -jar folderscan.jar
Loading config ...
Loading data ...
Scanning D:/Workspace ...
Comparing ...
In D:/Workspace/FolderScan/src/com/xrbpowered/folderscan:
   Modified: FolderScan.java
In D:/Workspace/FolderScan:
   Added: folderscan.data
Done
```
