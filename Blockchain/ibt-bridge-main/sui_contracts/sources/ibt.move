module ibt::ibt_token {
    use sui::coin::{Self, Coin, TreasuryCap};
    use sui::url;

    /// One-time witness
    public struct IBT_TOKEN has drop {}

    /// Initialize function - rulează la publish
    fun init(witness: IBT_TOKEN, ctx: &mut TxContext) {
        let (treasury, metadata) = coin::create_currency(
            witness,
            9, // decimals
            b"IBT",
            b"Inter Blockchain Token",
            b"Cross-chain bridge token (Local)",
            option::some(url::new_unsafe_from_bytes(b"https://example.com/ibt.png")),
            ctx
        );

        // Freeze metadata - devine immutable
        transfer::public_freeze_object(metadata);
        
        // Transfer treasury cap to deployer
        transfer::public_transfer(treasury, tx_context::sender(ctx));
    }

    /// Mint tokens - doar owner-ul TreasuryCap poate apela
    public entry fun mint(
        treasury: &mut TreasuryCap<IBT_TOKEN>,
        amount: u64,
        recipient: address,
        ctx: &mut TxContext
    ) {
        let coin = coin::mint(treasury, amount, ctx);
        transfer::public_transfer(coin, recipient);
    }

    /// Burn tokens
    public entry fun burn(
        treasury: &mut TreasuryCap<IBT_TOKEN>,
        coin: Coin<IBT_TOKEN>
    ) {
        coin::burn(treasury, coin);
    }
}
