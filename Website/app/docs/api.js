import fs from 'fs'
import { join } from 'path'
import matter from 'gray-matter'
import markdownToHtml from 'remark-html'
import { usePathname, useSearchParams } from 'next/navigation'

const componentsDirectory = join(process.cwd(), 'app/docs/pages')

export function getComponentNames() {
  return fs.readdirSync(componentsDirectory)
}

export function getComponentByName(name, fields = []) {
  const realName = name.replace(/\.md$/, '')
  const fullPath = join(componentsDirectory, `${realName}.md`)
  const fileContents = fs.readFileSync(fullPath, 'utf8')
  const { data, content } = matter(fileContents)

  const items = {}

  // Ensure only the minimal needed data is exposed
  fields.forEach((field) => {
    if (field === 'name') {
      items[field] = realName
    }
    if (field === 'content') {
      items[field] = content
    }
    if (data[field]) {
      items[field] = data[field]
    }
  })

  return items
}

export async function getAllComponents(fields = []) {
  const names = getComponentNames()
  const components = names.map((name) => getComponentByName(name, fields))
  // sort components by name
  components.sort((a, b) => (a.name > b.name ? 1 : -1))
  return components
}

export async function getComponentHtml(name) {
  const component = getComponentByName(name, ['content'])
  const contentHtml = await markdownToHtml(component.content || '')
  return contentHtml
}

// Use the new useRouter hook in your component
function Component() {
  const pathname = usePathname()
  const searchParams = useSearchParams()
  
  const name = pathname.split('/')[2]
  
  const componentData = getComponentByName(name)
  
  // render components here (i have lesser idea about this)
}
