// renders documentation with components

import Head from 'next/head'
import Markdown from 'react-markdown'
import { getAllComponents, getComponentHtml } from 'lib/api'

export default function Docs({ headerHtml, quickstartHtml, gettingStartedHtml, faqHtml, troubleshootingHtml }) {
  return (
    
    <>
      <Head>
        <title>Documentation</title>
      </Head>
      <ReactMarkdown children={headerHtml} />
      <main>
        <h1>Documentation</h1>
        <Markdown children={quickstartHtml} />
        <Markdown children={gettingStartedHtml} />
        <Markdown children={faqHtml} />
        <Markdown children={troubleshootingHtml} />
      </main>
    </>
  )
}

export async function docs() {
  const headerHtml = await getComponentHtml('header')
  const quickstartHtml = await getComponentHtml('quickstart')
  const gettingStartedHtml = await getComponentHtml('getting-started')
  const faqHtml = await getComponentHtml('faq')
  const troubleshootingHtml = await getComponentHtml('troubleshooting')
  return {
    props: {
      headerHtml,
      quickstartHtml,
      gettingStartedHtml,
      faqHtml,
      troubleshootingHtml,
    },
  }
}
