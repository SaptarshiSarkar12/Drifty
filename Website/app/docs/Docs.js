"use client";
import Link from "next/link";
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {AccordionDetails} from "@mui/material";
import {useState} from "react";
import {classNames} from "@/app/classNames";

export default function Docs() {
    const sections = [
        {
            title: "Quickstart",
            description: "The fastest way to get started with Drifty",
            href: "/docs/quickstart",
            hasChildren: true,
            children: [
                {  name: "Quickstart with Drifty GUI", href: "/docs/quickstart-drifty-gui" },
                {  name: "Quickstart with Drifty CLI", href: "/docs/quickstart-drifty-cli" }
            ]
        },
        {
            title: "Getting Started",
            description: "Learn how to get started with the project",
            href: "/docs/getting-started",
            hasChildren: true,
            children: [
                {  name: "Getting Started with Drifty GUI", href: "/docs/getting-started-drifty-gui" },
                {  name: "Getting Started with Drifty CLI", href: "/docs/getting-started-drifty-cli" }
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
    const [expanded, setExpanded] = useState();
    const handleChange = (section) => (event, newExpanded) => {
        setExpanded(newExpanded ? section : false);
    };

    return(
        <div className="text-center bg-gradient-to-b from-top to-bottom">
            <h1 className={"text-5xl p-5 pb-7"}>Documentation</h1>
            <div className={"grid grid-flow-col"}>
                <div className={"grid grid-cols-1"}>
                {sections.map((page) => {
                    if (page.hasChildren) {
                        return (
                            <Accordion expanded={expanded === page.title} onChange={handleChange(page.title)} elevation={0} disableGutters={true} key={page.title} className={"text-black border-none h-auto w-auto md:w-60"} sx={{
                                '&:before': {
                                    display: 'none'
                                },
                                backgroundColor: 'transparent',
                                '&.Mui-expanded': { margin: 0 },
                                padding: 0
                            }}>
                                <AccordionSummary key={page.title} className={classNames(
                                    expanded === page.title && "bg-blue-600 text-white",
                                    "hover:bg-blue-600 rounded m-2 border-none font-bold hover:text-white")}
                                    expandIcon={<ExpandMoreIcon/>}
                                >
                                    <h3>{page.title}</h3>
                                </AccordionSummary>
                                <AccordionDetails sx={{ maxWidth: '480px' }}>
                                    <div className={"grid grid-cols-1"}>
                                        {page.children.map((child, index) => {
                                            return (
                                                <Link key={index} className={"p-1 hover:text-blue-800 font-medium hover:font-bold rounded-lg"}
                                                      href={child.href}>
                                                    <h1 className={"text-sm"}>{child.name}</h1>
                                                </Link>
                                            )
                                        })}
                                    </div>
                                </AccordionDetails>
                            </Accordion>
                        )
                    } else {
                        return (
                            <Accordion expanded={expanded === page.title} onChange={handleChange(page.title)} elevation={0} disableGutters={true} key={page.title} className={"text-black border-none h-auto w-auto md:w-60"} sx={{
                                '&:before': {
                                    display: 'none'
                                },
                                backgroundColor: 'transparent',
                                '&.Mui-expanded': { margin: 0 },
                                padding: 0
                            }}>
                                <AccordionSummary key={page.title} className={classNames(
                                    expanded === page.title && "bg-blue-600 text-white",
                                    "hover:bg-blue-600 rounded m-2 border-none font-bold hover:text-white")}>
                                    <h3>{page.title}</h3>
                                </AccordionSummary>
                            </Accordion>
                        )
                    }
                })}
                </div>
                <div className={"grid grid-cols-2 gap-2 md:grid-cols-4 p-5"}>
                    {sections.map((page, index) => {
                        return (
                            <Link key={index} className={"w-auto h-36 border-2 border-white bg-white rounded-lg shadow-lg p-4 hover:border-blue-600"} href={page.href}>
                                <h2 className={"text-2xl"}>{page.title}</h2>
                                <label className={"text-sm"}>{page.description}</label>
                            </Link>
                        )
                    })}
                </div>
            </div>
        </div>
    )
}