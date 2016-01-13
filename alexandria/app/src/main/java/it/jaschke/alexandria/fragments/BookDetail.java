package it.jaschke.alexandria.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activity.MainActivity;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class BookDetail extends Fragment {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;

    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    @Nullable
    @Bind(R.id.right_container)
    FrameLayout rightFrameContainer;

    @Bind(R.id.fullBookTitle)
    TextView bookTitleTextView;

    @Bind(R.id.fullBookSubTitle)
    TextView bookSubTitleTextView;

    @Bind(R.id.fullBookDesc)
    TextView bookDescTextView;

    @Bind(R.id.authors)
    TextView authorsTextView;

    @Bind(R.id.categories)
    TextView categoriesTextView;

    @Bind(R.id.backButton)
    ImageButton backButton;

    @Bind(R.id.fullBookCover)
    ImageView bookCoverImageView;


    public BookDetail() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getActivity().getResources().getString(R.string.detail));
        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            restartLoader();
        }
        View rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.delete_button)
    public void deleteButtonClicked(View view) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if (MainActivity.IS_TABLET && rightFrameContainer == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void restartLoader() {
        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, bookDetailsLoaderCallbacks);
        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, bookDetailsLoaderCallbacks);
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> bookDetailsLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {
                return;
            }

            bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            bookTitleTextView.setText(bookTitle);

            if (shareActionProvider != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                } else {
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
                shareActionProvider.setShareIntent(shareIntent);
            }

            String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            bookSubTitleTextView.setText(bookSubTitle);

            String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            bookDescTextView.setText(desc);

            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            if (authors != null) {
                String[] authorsArr = authors.split(",");
                authorsTextView.setLines(authorsArr.length);
                authorsTextView.setText(authors.replace(",", "\n"));
            }
            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {

                Glide.with(getActivity())
                        .load(imgUrl).asBitmap()
                        .placeholder(R.drawable.ic_launcher)
                                //.error(R.drawable.ic_launcher)
                        .into(bookCoverImageView);

                bookCoverImageView.setVisibility(View.VISIBLE);
            }

            String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            categoriesTextView.setText(categories);

            if (rightFrameContainer != null) {
                backButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

        }
    };
}