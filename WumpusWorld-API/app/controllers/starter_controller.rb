class StarterController < ApplicationController

  include Map_data

  def init
    render json: { status: 0, data: get_positions }
  end

  def resume
    nickname = params[:nickname]

    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      render json: { status: 0, data: parse_states(player.state), arrow: player.arrow }
    else
      render json: { status: 1 }
    end
  end

  def start
    nickname = params[:nickname]
    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      player.update(is_playing: true, state: create_new_game, arrow: 3)
      render json: { status: 0, data: parse_states(player.state) }
    else
      player = Player.create(name: nickname, is_playing: true, state: create_new_game, arrow: 3)
      render json: { status: 0, data: parse_states(player.state) }
    end
  end

  private

  def create_new_game
    '00P0010P01000C000W0W00P0'
  end
end
