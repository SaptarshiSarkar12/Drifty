# Using the Drifty GUI Docker image on macOS

There are some steps to follow if you're going to run the Drifty GUI Docker image in macOS. At the end of these instructions, we will have a single command that we can run to start the Docker image.

## Prerequisites

You will need these programs to follow these instructions:

- [**Docker**](https://docs.docker.com/desktop/install/mac-install)
- [**HomeBrew**](https://brew.sh/)
- **Socat** (will be installed later in the instructions)
- **XQuartz** (will be installed later in the instructions)

## Installing the necessary programs and building/pulling the Docker image

Once you have Docker installed **(and running)**, open the terminal and follow these steps:

- Update the local Homebrew repository and upgrade any outdated installations. This ensures that any libraries that might be necessary will be up to date.
  ```bash
  brew update
  brew upgrade
  ```
- Install `Socat` and `XQuartz`
  ```bash
  brew install socat
  brew install xquartz
  ```
- To build the **Drifty GUI** Docker image using your changed source code, run this command in the directory where the project files are present:
  ```bash
  docker compose build gui
  ```
- If you are going to use the pre-built **Drifty GUI** Docker image from [**GitHub Container registry releases**](https://ghcr.io/saptarshisarkar12/drifty-gui), then run this command:
  ```bash
  docker pull ghcr.io/saptarshisarkar12/drifty-gui:master
  ```
  Remember to change the tag to the version/branch you want to use.

## Running the Docker image of Drifty GUI

After the Docker image of Drifty GUI has been built/pulled, we need to provide a pathway for the Docker instance to access the display. This can be done by providing it the IP address of your Mac. For simplicity and ease of use, manually (or statically) assigning an IP address to your Mac is suggested.

> [!NOTE]
>
> <details>
> <summary>
> Why static IP address is preferred for running Drifty GUI in Docker on macOS?
> </summary>
> Now that the image is built/pulled, we need to talk about how the GUI itself is going to be shown on your desktop. The only real relevant issue here pertains to your local IP address assigned to your network interface card on your Mac. You see, in a Linux environment (which macOS is modeled after), programs that run in the Terminal do not have direct access to your graphical environment (Graphical User Interface), so we are going to have to provide the Docker instance with a pathway to access the GUI and that has to be done by telling it what your local IP address is.
>
> On most home networks, the typical setup is to have a router where your computer is connected to the LAN (Local Area network) side of the router either via Wi-Fi or an Ethernet cable. And most people just make that connection, find that they can access the Internet and don't look back.
>
> We, however, need to look back...
>
> Routers use a service called DHCP (Dynamic Host Control Protocol) which uses a reserved pool of IP addresses. If any device connects to your network and when it is set up to use DHCP (which is always the default), it will send out a broadcast packet onto your network asking for an IP address. Your router will see that request, then it will take out an IP address from its pool, and it will give it to your device so that your device can talk on your network, access the Internet, etc.
>
> DHCP services are also designed to make that assigned IP address expire after some time (usually three days). In case a device that was on the network is no longer on the network, it can pull that IP address back and put it into the pool so that it doesn't run out of IP addresses. This means that it is possible your local IP address might change in a short period of time. We cannot build a command to run Drifty when your IP address is changing because docker needs to send the graphics to a known IP address.
>
> There are usually two generic ways to refer to your local ip address in a linux or Windows environment. The first way is to simply use the word `localhost` and the second way is to use the default home IP address of `127.0.0.1`. Unfortunately, neither of those generics will work in this case, so we have to give the command the exact IP address of your Mac.
>
> </details>

### Manually assigning an IP address to your Mac

We first need to know the IP address that the router has assigned to your Mac. Most of the routers will use an IP address that starts with `192.168` so let's see if we have that kind of address assigned to our Mac, by running this command:

```bash
ifconfig | grep 192.168
```

You should see something like this:

> inet 192.168.1.123 netmask 0xffffff00 broadcast 192.168.1.255

The **IP address** that is after the word `inet` will be the address that your router assigned to your Mac. Since that address exists in the router's pool of IP addresses, so we cannot statically assign that address to your Mac. So I recommend assigning an address that is lower in value, which has a better chance of being outside the router's address pool. I would use an address like `192.168.1.10`, so we will go with that.

If you are not running at **_least macOS version 13 (Ventura)_**, then refer to [**this article**](https://www.macinstruct.com/tutorials/how-to-set-a-static-ip-address-on-a-mac/) to get the instructions for **statically assigning your IP address**. Otherwise, follow these instructions:

- Go to `System Settings` by clicking on the Apple logo in the top-left corner of your screen.
- Click on `Network` then click on your network adapter.
  ![Network Settings' screenshot](https://github.com/user-attachments/assets/f2f54273-1bf4-4286-bc56-88991eb4df84)
- Click on `Details`  
  ![Details of Network Adapter](https://github.com/user-attachments/assets/d3a43e3f-616d-4286-a527-d1bc2ecbb207)
- Now you need to change `Configure IPv4` to `Manually` and then type your **static IP address**, **subnet mask** and **gateway**. The subnet mask will look exactly like I have it here, regardless of your ip address and your router will always be `.1` (in most cases) as shown here.  
  ![Configuring IPv4 manually](https://github.com/user-attachments/assets/c7358318-fb90-4554-86c2-6629d7c9476a)
- Click `OK` and then go back to your Terminal.

### Change the XQuartz settings

A small change in XQuartz is necessary for running **Drifty GUI** via _Docker_. Follow these steps:

- Open **XQuartz** by running this command:
  ```bash
  open -a Xquartz
  ```
- When XQuartz loads, you will see a window that looks like this:
  ![XQuartz initial window](https://github.com/user-attachments/assets/762bacba-7770-4590-8396-93c9bc31ace3)
- Click on the XQuartz menu at the top-left of your screen just next to the Apple logo and select `Settings`. Click on the `Security` tab and then check `Allow connections from network clients` as shown below:
  ![XQuartz Security window](https://github.com/user-attachments/assets/43fba116-030e-47bf-b5a1-17d09dd64e31)

### Commands to run the Docker image

We have finally set up everything required to run the Docker image of **Drifty GUI**. Now, we need to run the following commands in the Terminal:

- If you want to run the **pre-built** docker image, then run these commands:
  ```bash
  socat TCP-LISTEN:6000,reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\"&
  open -a Xquartz
  docker run -e DISPLAY=192.168.1.10:0 ghcr.io/saptarshisarkar12/drifty-gui:master
  ```
  Ensure to update the tag to the desired version/branch.
- To run the **docker image built from your changed source code** (using `docker compose`), execute the following commands:
  ```bash
  socat TCP-LISTEN:6000,reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\"&
  open -a Xquartz
  docker compose run -e DISPLAY=192.168.1.10:0 gui
  ```
