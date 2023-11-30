## PicKit2-GUI-Linux
Simple graphical user interface for pk2cmd on Linux.

![](/doc/PicKit2-GUI-Linux.png)

### Features
- Auto-detection of the PIC on your board.
- Can write, run, stop, erase and test a .hex on your board.
- Check the firmware of PicKit 2 and file versions
- Remembers where you chose the .hex even after closing the software :)

### Known bugs/restrictions
- No HEX editor. I don't have any use for it at school so I haven't done it.
- Not possible to transfer running .hex on hard disk.
- You need to install USB driver (see installation notes)
- Some freezes can be seen during process. Don't panic, the PicKit is working and you'll see the output in the logs.

### Installation
- Install **libusb** driver, open terminal and type `sudo apt-get install libusb-0.1-4:i386`
- Run the software, `java -jar PicKit2-Programmer-1.0.jar`
- Enter your root password and you're done.

### Disclaimer
No warranty will be made for this software. Use it at your own risks!
This software is free.
This software uses a recompiled version of `pk2cmd` from Microchip and a custom `PK2DeviceFile.dat` from the Microchip's forums.

### Developer
- Chayan Mistry
- chayanmistrry@gmail.com