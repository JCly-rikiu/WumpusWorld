class AddStatePlayers < ActiveRecord::Migration
  def change
    add_column :players, :state, :text
  end
end
