export interface WordPressRendered {
  rendered: string;
}

export interface WordPressEmbeddedMedia {
  id: number;
  source_url?: string;
  media_details?: {
    sizes?: {
      medium?: { source_url: string };
      large?: { source_url: string };
      thumbnail?: { source_url: string };
    };
  };
}

export interface WordPressEmbeddedTerm {
  id: number;
  name: string;
  slug: string;
  taxonomy: string;
}

export interface WordPressPost {
  id: number;
  date: string;
  modified: string;
  slug: string;
  link: string;
  title: WordPressRendered;
  content: WordPressRendered;
  excerpt: WordPressRendered;
  categories: number[];
  _embedded?: {
    'wp:featuredmedia'?: WordPressEmbeddedMedia[];
    'wp:term'?: WordPressEmbeddedTerm[][];
  };
}

export interface WordPressCategory {
  id: number;
  count: number;
  description: string;
  link: string;
  name: string;
  slug: string;
}

export interface SavedPost {
  id: number;
  title: string;
  excerpt: string;
  content: string;
  link: string;
  date: string;
  modified: string;
  imageUrl?: string;
  categoryName?: string;
  savedAt: number;
}
