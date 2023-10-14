import Header from '../Header'
import Footer from '../Footer'
import Markdown from 'react-markdown'
import { getAllComponents, getComponentHtml } from './api'

export const metadata = {
    title: 'Documentation',
    description: 'Documentation for the Drifty project',
    themeColor: [{ media: '(prefers-color-scheme: dark)', color: 'Medium Blue' }],
    viewport: {
        width: 'device-width',
        initialScale: 1,
    },
}

export default async function Docs() {
    const headerHtml = await getComponentHtml('header.md')
    const quickstartHtml = await getComponentHtml('quickstart.md')
    const gettingStartedHtml = await getComponentHtml('getting-started.md')
    const faqHtml = await getComponentHtml('faq.md')
    const troubleshootingHtml = await getComponentHtml('troubleshooting.md')
    return (
        <div>
            <Header props={'bg-top'} />
            <main>
                <h1>Documentation</h1>
                <Markdown children={quickstartHtml} />
                <Markdown children={gettingStartedHtml} />
                <Markdown children={faqHtml} />
                <Markdown children={troubleshootingHtml} />
            </main>
            <Footer />
        </div>
    )
}