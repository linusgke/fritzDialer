# FRITZ!Dialer

Dial any phone number on a telephone connected to your FRITZ!Box.

<img width="393" height="367" alt="grafik" src="https://github.com/user-attachments/assets/8a035e7f-20d5-42e6-b331-6af946c6bb63" />

## How to use

1. Select the desired phone number (just select, no copying necessary!)

2. Press the configured key combination (default: Ctrl + Y)<br>
   _(By pressing Ctrl + B you can also call a phone number currently saved in your clipboard.)_

4. If configured, a dialog opens, asking to select to phone from where the selected number is to be called.<br>
   _If not, the number is directly called from the selected phone_

<img width="558" height="392" alt="grafik" src="https://github.com/user-attachments/assets/f9fdbd1a-a8d0-4d0d-9ee9-4fc1c9805aae" />

5. Your phone should _ring_ now! Upon answering, dialing starts.<br>
   _(Depending on your type phone, it might only start ringing whenever the called party picks up the call. See ['Known restrictions'](https://github.com/linusgke/FRITZDialer/edit/master/README.md#known-restrictions))_

# Known restrictions

- IP phones aren't compatible!<br>This is due to a limitation in the FRITZ!Box API.

- Some phones (older DECT phones) only start ringing when the called party picked up the call.<br>
In order to prevent the called party from hearing _waiting music_ until you pick up, you can join the call early by pressing the _green call button_ on your phone for 1-2 seconds _right after_ placing the request from your machine.
