import { WordPressPost, WordPressCategory } from '../types';

const BASE_URL = 'https://nsguruji.com/wp-json/wp/v2';

export async function fetchLatestPosts(page = 1, perPage = 10, categoryId?: number): Promise<{ posts: WordPressPost[]; totalPages: number }> {
  try {
    let url = `${BASE_URL}/posts?_embed=true&per_page=${perPage}&page=${page}`;
    if (categoryId) {
      url += `&categories=${categoryId}`;
    }

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Failed to fetch posts: ${response.status} ${response.statusText}`);
    }

    const totalPages = parseInt(response.headers.get('X-WP-TotalPages') || '1', 10);
    const posts: WordPressPost[] = await response.json();
    return { posts, totalPages };
  } catch (error) {
    console.error('Error in fetchLatestPosts:', error);
    throw error;
  }
}

export async function fetchCategories(): Promise<WordPressCategory[]> {
  try {
    const response = await fetch(`${BASE_URL}/categories?per_page=50&hide_empty=true`);
    if (!response.ok) {
      throw new Error(`Failed to fetch categories: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error in fetchCategories:', error);
    throw error;
  }
}

export async function searchPosts(query: string, page = 1): Promise<WordPressPost[]> {
  try {
    const encoded = encodeURIComponent(query.trim());
    const response = await fetch(`${BASE_URL}/posts?_embed=true&search=${encoded}&per_page=15&page=${page}`);
    if (!response.ok) {
      throw new Error(`Failed to search posts: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error in searchPosts:', error);
    throw error;
  }
}

export async function fetchPostById(id: number): Promise<WordPressPost> {
  try {
    const response = await fetch(`${BASE_URL}/posts/${id}?_embed=true`);
    if (!response.ok) {
      throw new Error(`Failed to fetch post #${id}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error in fetchPostById:', error);
    throw error;
  }
}

export async function fetchPostBySlug(slug: string): Promise<WordPressPost | null> {
  try {
    const response = await fetch(`${BASE_URL}/posts?_embed=true&slug=${encodeURIComponent(slug)}`);
    if (!response.ok) return null;
    const posts: WordPressPost[] = await response.json();
    return posts.length > 0 ? posts[0] : null;
  } catch (error) {
    console.error('Error in fetchPostBySlug:', error);
    return null;
  }
}

// Helpers for clean text
export function cleanHtmlText(html: string): string {
  if (!html) return '';
  const div = document.createElement('div');
  div.innerHTML = html;
  return div.textContent || div.innerText || '';
}

export function getFeaturedImageUrl(post: WordPressPost): string | null {
  return post._embedded?.['wp:featuredmedia']?.[0]?.source_url ||
    post._embedded?.['wp:featuredmedia']?.[0]?.media_details?.sizes?.large?.source_url ||
    post._embedded?.['wp:featuredmedia']?.[0]?.media_details?.sizes?.medium?.source_url ||
    null;
}

export function getPostCategoryName(post: WordPressPost): string {
  const terms = post._embedded?.['wp:term']?.[0];
  if (terms && terms.length > 0) {
    return cleanHtmlText(terms[0].name);
  }
  return 'सरकारी योजना व अपडेट्स';
}

export function formatPostDate(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleDateString('hi-IN', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  } catch {
    return isoDate;
  }
}
