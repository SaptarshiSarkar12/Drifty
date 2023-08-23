"use client";
import { Tab } from "@headlessui/react"

function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}

export default function Demo(){
    const tabs = ["Drifty CLI", "Drifty GUI"];
    return(
        <div id="demo" className="bg-bottom">
            <h1 className="select-none text-center font-extrabold text-4xl pt-6">Demonstration of Drifty</h1>
            <p className="select-none text-center text-black text-2xl py-3">Here is a quick demo of Drifty</p>
            <div className={"text-center"}>
                <Tab.Group>
                    <Tab.List className={"space-x-2"}>
                        {tabs.map((tab) => (
                            <Tab className={({ selected }) =>
                                classNames(
                                    'w-44 rounded-full py-2.5 font-extrabold leading-5 text-blue-700',
                                    'ring-blue ring-opacity-60 ring-offset-2 ring-offset-bottom focus:outline-none focus:ring-2',
                                    selected
                                        ? 'text-white bg-blue-600 shadow'
                                        : 'hover:bg-gray-700/[0.12]'
                                )
                            } key={tab}>{tab}</Tab>
                        ))}
                    </Tab.List>
                    <Tab.Panels>
                        <Tab.Panel className={classNames(
                            'rounded-xl bg-bottom p-3',
                            'focus:outline-none focus:ring-0'
                        )}>
                            <div className="flex justify-center">
                                <video width="80%" muted autoPlay loop>
                                    <source src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Usage.webm" type='video/webm'/>
                                    Your browser does not support the video tag
                                </video>
                            </div>
                        </Tab.Panel>
                        <Tab.Panel>
                            GUI video here
                        </Tab.Panel>
                    </Tab.Panels>
                </Tab.Group>
            </div>
        </div>
    )
}
