// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import {Script, console} from "forge-std/Script.sol";
import {IBTToken} from "../src/IBTToken.sol";

contract DeployScript is Script {
    function run() external returns (IBTToken) {
        vm.startBroadcast();
        
        IBTToken token = new IBTToken();
        
        console.log("================================");
        console.log("IBT Token Deployed!");
        console.log("================================");
        console.log("Contract Address:", address(token));
        console.log("Deployer:", msg.sender);
        console.log("Initial Supply:", token.totalSupply() / 10**18, "IBT");
        console.log("================================");
        
        vm.stopBroadcast();
        
        return token;
    }
}
