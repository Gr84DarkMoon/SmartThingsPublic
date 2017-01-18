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
    name: "Basement Light Timeout",
    namespace: "Gr84DarkMoon",
    author: "Gr84DarkMoon",
    description: "Turn something on when an open/close sensor opens.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("Contact to monitor:"){
		input "contact1", "capability.contactSensor", title: "Door?"
	}
	section("Switch to turn on:"){
		input "switches", "capability.switch", title: "Lights?", multiple: true
	}
    section("Delay after closed:"){
		input "delaySeconds", "number", title: "Seconds?"
	}
}


def installed()
{
	subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactCloseHandler)
    subscribe(switches, "switch", switchOnHandler, [filterEvents: false])
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactCloseHandler)
    subscribe(switches, "switch", switchOnHandler, [filterEvents: false])
}

def contactOpenHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "contactOpenHandler"
    unschedule(turnOffAfterDelay)
	switches.on()
}

def contactCloseHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "ScontactCloseHandler"
	runIn(delaySeconds, turnOffAfterDelay, [overwrite: true])
}

def turnOffAfterDelay() {
	log.trace "turnOffAfterDelay"
	switches.off()
}

def switchOnHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "switchOnHandler"
	unschedule(turnOffAfterDelay)
}