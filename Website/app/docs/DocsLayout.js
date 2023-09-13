"use client";
import Link from "next/link";
import { Disclosure } from '@headlessui/react'
import { ChevronRightIcon } from '@heroicons/react/20/solid'
import {classNames} from "@/app/classNames";
import Header from "@/app/Header";
import Footer from "@/app/Footer";

const sections = [
    {
        title: "Quickstart",
        description: "The fastest way to get started with Drifty",
        href: "/docs/quickstart",
        hasChildren: true,
        children: [
            {  name: "Drifty GUI", href: "/docs/quickstart-drifty-gui" },
            {  name: "Drifty CLI", href: "/docs/quickstart-drifty-cli" }
        ]
    },
    {
        title: "Getting Started",
        description: "Learn how to get started with the project",
        href: "/docs/getting-started",
        hasChildren: true,
        children: [
            {  name: "Drifty GUI", href: "/docs/getting-started-drifty-gui" },
            {  name: "Drifty CLI", href: "/docs/getting-started-drifty-cli" }
        ]
    },
    {
        title: "FAQ",
        description: "Frequently asked questions",
        href: "/docs/faq",
        hasChildren: false
    },
    {
        title: "Contributing",
        description: "Learn how to contribute to the project",
        href: "/docs/contributing",
        hasChildren: false
    },
    {
        title: "Troubleshooting",
        description: "Learn how to troubleshoot common errors",
        href: "/docs/troubleshooting",
        hasChildren: false
    }
];

export default function DocsLayout({children, className}) {
    return(
        <>
            <Header props={"bg-top"}/>
            <div className="text-center bg-gradient-to-b from-top to-bottom">
                <h1 className={"text-5xl xs:text-4xl p-5 pb-7"}>Documentation</h1>
                <hr className="h-px border-t-0 bg-transparent bg-gradient-to-r from-transparent via-black to-transparent" />
                <div className={className}>
                    <div className={"grid grid-cols-1 pt-2"}>
                    {sections.map((page) => {
                        if (page.hasChildren) {
                            return (
                                <Disclosure key={page.title}>
                                    {({ open }) => (
                                        <>
                                            <Disclosure.Button className={classNames(
                                                open ? 'bg-blue-600 text-white' : '',
                                                'grid grid-flow-col justify-center text-black border-none  md:w-60 hover:bg-blue-600 rounded p-1 font-bold hover:text-white')}>
                                                {page.title}
                                                <ChevronRightIcon
                                                    className={classNames(
                                                        open ? 'transform rotate-90' : '',
                                                        'w-5 h-5 text-black'
                                                    )}
                                                />
                                            </Disclosure.Button>
                                            <Disclosure.Panel className="px-4 pt-4 pb-2 text-sm text-gray-500">
                                                <div className={"grid grid-cols-1"}>
                                                    {page.children.map((child, index) => {
                                                        return (
                                                            <Link key={index} className={"p-1 hover:text-blue-800 font-medium hover:font-bold rounded-lg"}
                                                                  href={child.href}>
                                                                <h1 className={"text-xl md:text-sm"}>{child.name}</h1>
                                                            </Link>
                                                        )
                                                    })}
                                                </div>
                                            </Disclosure.Panel>
                                        </>
                                    )}
                                </Disclosure>
                                // <Accordion key={page.title} className={"text-black border-none h-auto w-auto md:w-60"} onChange={handleChange(page.title)}>
                                //     <AccordionItem key={page.title} className={classNames(
                                //         expanded === page.title && "bg-blue-600 text-white",
                                //         "hover:bg-blue-600 rounded m-2 font-bold hover:text-white")}
                                //          title={page.title}
                                //     >
                                //         <div className={"grid grid-cols-1"}>
                                //             {page.children.map((child, index) => {
                                //                 return (
                                //                     <Link key={index} className={"p-1 hover:text-blue-800 font-medium hover:font-bold rounded-lg"}
                                //                           href={child.href}>
                                //                         <h1 className={"text-xl md:text-sm"}>{child.name}</h1>
                                //                     </Link>
                                //                 )
                                //             })}
                                //         </div>
                                //     </AccordionItem>
                                // </Accordion>
                            )
                        } else {
                            return (
                                <Link key={page.title} className={"pl-2 hover:text-blue-800 font-medium hover:font-bold rounded-lg"} href={page.href}>
                                    <Disclosure>
                                        <Disclosure.Button className={"flex justify-center text-black border-none h-auto w-auto md:w-60 hover:bg-blue-600 rounded p-2 font-bold hover:text-white"}>
                                            {page.title}
                                        </Disclosure.Button>
                                    </Disclosure>
                                </Link>
                            )
                        }
                    })}
                    </div>
                    <div className={"p-2"}>
                        {children}
                    </div>
                </div>
            </div>
            <Footer />
        </>
    )
}