/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Turn It On When It Opens
 *
 *  Author: SmartThings
 */
definition(
    name: "Fireplace Controller",
    namespace: "Gr84DarkMoon",
    author: "Gr84DarkMoon",
    description: "Turns on the fan when the fireplace turns on. Turns off the fan after dalay whe fireplace is turned off.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("Sensor Setup:"){
    	paragraph "When the fireplace switch is turned on, the fireplace fan will also turn on.  When the fireplace switch is turned off, the fan will turn off after the configured delay."
	}
	section("Sensor Setup:"){
		input "contact1", "capability.contactSensor", title: "Manual Switch?"
	}
	section("Switch Setup and Behavior:"){
		input "switches", "capability.switch", title: "Fireplace Flame?", multiple: true
        input "switchesdelay", "capability.switch", title: "Fireplace Fan? (Delayed Off)", multiple: true
        input "delaySeconds", "number", title: "Delayed Off in Seconds?"
    }
    
    section("Send Push Notifications?") {
        input "sendPushOn", "bool", required: false,
              title: "When fireplace goes On?"
        input "sendPushOff", "bool", required: false,
              title: "When fireplace goes Off?"
    }

}


def installed()
{
    subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactCloseHandler)
    subscribe(switches, "switch.on", switchOnHandler, [filterEvents: false])
    subscribe(switches, "switch.off", switchOffHandler, [filterEvents: false])
}

def updated()
{
    unsubscribe()
    subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactCloseHandler)
    subscribe(switches, "switch.on", switchOnHandler, [filterEvents: false])
    subscribe(switches, "switch.off", switchOffHandler, [filterEvents: false])
}

def contactOpenHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "contactOpenHandler"
	switches.off()
}

def contactCloseHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "ScontactCloseHandler"
    unschedule(turnOffAfterDelay)
    switches.on()
}

def turnOffAfterDelay() {
	log.trace "turnOffAfterDelay"
	switchesdelay.off()
}

def switchOnHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "switchOnHandler"
   	unschedule(turnOffAfterDelay) 
    switchesdelay.on()  
    if (sendPushOn) {
        sendPush("The ${switches.displayName} turned ON!")
    }
}

def switchOffHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "switchOnHandler"
    runIn(delaySeconds, turnOffAfterDelay, [overwrite: true]) 
    if (sendPushOff) {
        sendPush("The ${switches.displayName} turned OFF!")
    }

}