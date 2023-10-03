// renders homepage from header
import Head from 'next/head'
import ReactMarkdown from 'react-markdown'
import { getComponentHtml } from '../lib/api'

export default function Home({ headerHtml }) {
  return (
    <>
      <Head>
        <title>DriftShifty</title>
      </Head>
      <ReactMarkdown children={headerHtml} />
      <main>
        <h1>Welcome to DriftShifty</h1>
        <p>This is a website that helps you shift your drifts.</p>
      </main>
    </>
  )
}

export async function getStaticProps() {
  const headerHtml = await getComponentHtml('header')
  return {
    props: {
      headerHtml,
    },
  }
}
