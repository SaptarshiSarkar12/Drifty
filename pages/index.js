// renders homepage from header
import Head from 'next/head'
import ReactMarkdown from 'react-markdown'
import { getComponentHtml } from '../lib/api'
import { useRouter } from 'next/router'

export default function Home({ headerHtml }) {
  const router = useRouter()
  
  //this makes us access the component
  const { name } = router.query
  
  //this will fetch the data
  const componentData = getComponentByName(name)
  
  return (
    <>
      <Head>
        <title>DriftShifty</title>
      </Head>
      <ReactMarkdown children={headerHtml} />
      <main>
        <h1>Welcome to Drifty</h1>
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
