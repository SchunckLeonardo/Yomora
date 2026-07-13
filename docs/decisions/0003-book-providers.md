# ADR 0003: Google Books com fallback Open Library

Status: aceito.

Google Books é a fonte primária pela cobertura e metadados. Open Library é consultada quando a primária falha ou não encontra resultados. Uma porta `BookProvider` permite trocar ou adicionar fontes sem alterar o domínio.

